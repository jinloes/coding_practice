package com.jinloes.system_design.ratelimit;

import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Per-key token-bucket rate limiter supporting a sustained refill rate with a bounded burst.
 *
 * <p>See {@code docs/patterns/rate-limiting.md}.
 */
public final class TokenBucketRateLimiter {
    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private final long burstCapacity;
    private final double refillTokensPerSecond;
    private final Clock clock;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(long burstCapacity, double refillTokensPerSecond, Clock clock) {
        if (burstCapacity <= 0) {
            throw new IllegalArgumentException("burstCapacity must be positive");
        }
        if (!(refillTokensPerSecond > 0.0) || Double.isInfinite(refillTokensPerSecond)) {
            throw new IllegalArgumentException("refillTokensPerSecond must be finite and positive");
        }
        this.burstCapacity = burstCapacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public RateLimitDecision tryAcquire(String key) {
        return tryAcquire(key, 1L);
    }

    public RateLimitDecision tryAcquire(String key, long permits) {
        Objects.requireNonNull(key, "key must not be null");
        if (permits <= 0 || permits > burstCapacity) {
            throw new IllegalArgumentException("permits must be between 1 and burstCapacity");
        }
        long nowNanos = currentEpochNanos();
        AtomicReference<RateLimitDecision> decisionRef = new AtomicReference<>();
        buckets.compute(key, (ignored, existing) -> {
            Bucket current = existing == null
                ? new Bucket(burstCapacity, nowNanos)
                : existing.refill(nowNanos, burstCapacity, refillTokensPerSecond);
            if (current.tokens >= permits) {
                double updatedTokens = current.tokens - permits;
                decisionRef.set(new RateLimitDecision(true, (long) Math.floor(updatedTokens), Duration.ZERO));
                return new Bucket(updatedTokens, current.lastRefillNanos);
            }
            double deficit = permits - current.tokens;
            long waitNanos = (long) Math.ceil((deficit / refillTokensPerSecond) * NANOS_PER_SECOND);
            decisionRef.set(new RateLimitDecision(
                false,
                (long) Math.floor(current.tokens),
                Duration.ofNanos(Math.max(1L, waitNanos))
            ));
            return current;
        });
        return decisionRef.get();
    }

    private long currentEpochNanos() {
        var now = clock.instant();
        return Math.multiplyExact(now.getEpochSecond(), 1_000_000_000L) + now.getNano();
    }

    private record Bucket(double tokens, long lastRefillNanos) {
        Bucket refill(long nowNanos, long capacity, double refillPerSecond) {
            long elapsedNanos = Math.max(0L, nowNanos - lastRefillNanos);
            if (elapsedNanos == 0L) {
                return this;
            }
            double added = (elapsedNanos / NANOS_PER_SECOND) * refillPerSecond;
            return new Bucket(Math.min(capacity, tokens + added), nowNanos);
        }
    }
}
