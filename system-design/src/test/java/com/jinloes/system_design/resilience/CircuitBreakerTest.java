package com.jinloes.system_design.resilience;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CircuitBreakerTest {
    @Test
    void transitionsThroughClosedOpenHalfOpenAndRecoversOrRetrips() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        CircuitBreaker breaker = new CircuitBreaker(2, Duration.ofSeconds(30), 2, clock);

        // Single failure followed by success resets consecutive failure count.
        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new IllegalStateException("timeout-1");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
        assertThat(breaker.execute(() -> "ok")).isEqualTo("ok");

        // Two consecutive failures trip the breaker OPEN.
        for (int i = 0; i < 2; i++) {
            assertThatThrownBy(() -> breaker.execute(() -> {
                throw new IllegalStateException("downstream-503");
            })).isInstanceOf(IllegalStateException.class);
        }
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.OPEN);
        assertThatThrownBy(() -> breaker.execute(() -> "should-not-run"))
            .isInstanceOf(CircuitBreaker.CallNotPermittedException.class);

        // After cooldown, breaker enters HALF_OPEN; a failed probe immediately re-opens it.
        clock.advance(Duration.ofSeconds(30));
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new IllegalStateException("still-broken");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.OPEN);

        // Wait another cooldown and succeed on all required probes -> CLOSED.
        clock.advance(Duration.ofSeconds(30));
        assertThat(breaker.execute(() -> "probe-1")).isEqualTo("probe-1");
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
        assertThat(breaker.execute(() -> "probe-2")).isEqualTo("probe-2");
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void capsConcurrentHalfOpenProbes() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        CircuitBreaker breaker = new CircuitBreaker(1, Duration.ofSeconds(10), 1, clock);

        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new RuntimeException("boom");
        })).isInstanceOf(RuntimeException.class);

        clock.advance(Duration.ofSeconds(10));
        String outerResult = breaker.execute(() -> {
            // While the single half-open probe is in flight, concurrent callers fast-fail.
            assertThatThrownBy(() -> breaker.execute(() -> "concurrent-probe"))
                .isInstanceOf(CircuitBreaker.CallNotPermittedException.class);
            return "recovered";
        });
        assertThat(outerResult).isEqualTo("recovered");
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void staleHalfOpenSuccessCannotCompleteANewerRecoveryWindow() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        CircuitBreaker breaker = new CircuitBreaker(1, Duration.ofSeconds(10), 2, clock);
        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new IllegalStateException("initial failure");
        })).isInstanceOf(IllegalStateException.class);
        clock.advance(Duration.ofSeconds(10));

        assertThat(breaker.execute(() -> {
            assertThatThrownBy(() -> breaker.execute(() -> {
                throw new IllegalStateException("failed concurrent probe");
            })).isInstanceOf(IllegalStateException.class);
            clock.advance(Duration.ofSeconds(10));
            assertThat(breaker.execute(() -> "new probe")).isEqualTo("new probe");
            return "old probe";
        })).isEqualTo("old probe");

        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.HALF_OPEN);
        assertThat(breaker.execute(() -> "second new probe")).isEqualTo("second new probe");
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void staleHalfOpenFailureCannotReopenARecoveredGeneration() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        CircuitBreaker breaker = new CircuitBreaker(1, Duration.ofSeconds(10), 2, clock);
        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new IllegalStateException("initial failure");
        })).isInstanceOf(IllegalStateException.class);
        clock.advance(Duration.ofSeconds(10));
        Error staleFailure = new AssertionError("old probe failed");

        assertThatThrownBy(() -> breaker.execute(() -> {
            assertThatThrownBy(() -> breaker.execute(() -> {
                throw new IllegalStateException("failed concurrent probe");
            })).isInstanceOf(IllegalStateException.class);
            clock.advance(Duration.ofSeconds(10));
            breaker.execute(() -> "new probe one");
            breaker.execute(() -> "new probe two");
            throw staleFailure;
        })).isSameAs(staleFailure);

        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    @Test
    void staleClosedSuccessCannotResetFailuresInANewerClosedGeneration() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        CircuitBreaker breaker = new CircuitBreaker(2, Duration.ofSeconds(10), 1, clock);
        assertThat(breaker.execute(() -> {
            for (int i = 0; i < 2; i++) {
                assertThatThrownBy(() -> breaker.execute(() -> {
                    throw new IllegalStateException("trip");
                })).isInstanceOf(IllegalStateException.class);
            }
            clock.advance(Duration.ofSeconds(10));
            breaker.execute(() -> "recovery");
            assertThatThrownBy(() -> breaker.execute(() -> {
                throw new IllegalStateException("new generation failure");
            })).isInstanceOf(IllegalStateException.class);
            return "old success";
        })).isEqualTo("old success");

        assertThatThrownBy(() -> breaker.execute(() -> {
            throw new IllegalStateException("second new generation failure");
        })).isInstanceOf(IllegalStateException.class);
        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void staleClosedFailureCannotTripANewerClosedGeneration() {
        MutableClock clock = new MutableClock(Instant.EPOCH);
        CircuitBreaker breaker = new CircuitBreaker(1, Duration.ofSeconds(10), 1, clock);
        RuntimeException staleFailure = new IllegalStateException("old closed call failed");

        assertThatThrownBy(() -> breaker.execute(() -> {
            assertThatThrownBy(() -> breaker.execute(() -> {
                throw new IllegalStateException("current failure");
            })).isInstanceOf(IllegalStateException.class);
            clock.advance(Duration.ofSeconds(10));
            breaker.execute(() -> "recovered");
            throw staleFailure;
        })).isSameAs(staleFailure);

        assertThat(breaker.state()).isEqualTo(CircuitBreaker.State.CLOSED);
    }

    private static final class MutableClock extends Clock {
        private Instant instant;

        MutableClock(Instant initial) {
            this.instant = initial;
        }

        void advance(Duration delta) {
            instant = instant.plus(delta);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
