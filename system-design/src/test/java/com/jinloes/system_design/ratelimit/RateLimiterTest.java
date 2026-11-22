package com.jinloes.system_design.ratelimit;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RateLimiterTest {
    @Test
    void tokenBucketAllowsBoundedBurstThenRefillsAtSustainedRate() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:00Z"));
        TokenBucketRateLimiter limiter = new TokenBucketRateLimiter(3, 2.0, clock);

        assertThat(limiter.tryAcquire("tenant-a")).isEqualTo(new RateLimitDecision(true, 2, Duration.ZERO));
        assertThat(limiter.tryAcquire("tenant-a")).isEqualTo(new RateLimitDecision(true, 1, Duration.ZERO));
        assertThat(limiter.tryAcquire("tenant-a")).isEqualTo(new RateLimitDecision(true, 0, Duration.ZERO));

        RateLimitDecision rejected = limiter.tryAcquire("tenant-a");
        assertThat(rejected.allowed()).isFalse();
        assertThat(rejected.remaining()).isZero();
        assertThat(rejected.retryAfter()).isEqualTo(Duration.ofMillis(500));

        // Independent keys do not share token buckets.
        assertThat(limiter.tryAcquire("tenant-b").allowed()).isTrue();

        // Advancing 500 ms refills 1 token (2 tokens/sec), capping at burstCapacity after long idle.
        clock.advance(Duration.ofMillis(500));
        assertThat(limiter.tryAcquire("tenant-a")).isEqualTo(new RateLimitDecision(true, 0, Duration.ZERO));

        clock.advance(Duration.ofSeconds(10));
        assertThat(limiter.tryAcquire("tenant-a", 3)).isEqualTo(new RateLimitDecision(true, 0, Duration.ZERO));
    }

    @Test
    void slidingWindowCounterBlocksBoundaryBurstThatFixedWindowsWouldAllow() {
        MutableClock clock = new MutableClock(Instant.parse("2026-01-01T00:00:50Z"));
        SlidingWindowCounterRateLimiter limiter = new SlidingWindowCounterRateLimiter(
            10,
            Duration.ofSeconds(60),
            clock
        );

        // Exhaust all 10 requests in the final 10 seconds of minute 0.
        for (int i = 0; i < 10; i++) {
            assertThat(limiter.tryAcquire("client-1").allowed()).isTrue();
        }
        assertThat(limiter.tryAcquire("client-1").allowed()).isFalse();

        // Cross into minute 1 by 15 seconds (25% into new window -> 75% weight on previous 10 = 7.5).
        clock.advance(Duration.ofSeconds(25));
        assertThat(limiter.tryAcquire("client-1").allowed()).isTrue();  // 7.5 + 1 = 8.5 <= 10
        assertThat(limiter.tryAcquire("client-1").allowed()).isTrue();  // 7.5 + 2 = 9.5 <= 10

        RateLimitDecision blocked = limiter.tryAcquire("client-1");     // 7.5 + 3 = 10.5 > 10
        assertThat(blocked.allowed()).isFalse();
        assertThat(blocked.retryAfter()).isEqualTo(Duration.ofSeconds(3));

        clock.advance(blocked.retryAfter());
        assertThat(limiter.tryAcquire("client-1").allowed()).isTrue();
    }

    @Test
    void rejectsInvalidRateLimiterArguments() {
        Clock clock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC);

        assertThatThrownBy(() -> new TokenBucketRateLimiter(0, 1.0, clock))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new TokenBucketRateLimiter(5, 0.0, clock))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SlidingWindowCounterRateLimiter(0, Duration.ofSeconds(1), clock))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new SlidingWindowCounterRateLimiter(5, Duration.ZERO, clock))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new RateLimitDecision(false, -1, Duration.ZERO))
            .isInstanceOf(IllegalArgumentException.class);
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
        public Clock withZone(java.time.ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return instant;
        }
    }
}
