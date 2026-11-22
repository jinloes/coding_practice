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
