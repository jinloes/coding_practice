package com.jinloes.system_design.ratelimit;

import java.math.BigInteger;
import java.time.Clock;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Weighted sliding-window counter rate limiter that smooths fixed-window boundary bursts
 * using only two counters per key rather than a per-request timestamp log.
 *
 * <p>See {@code docs/patterns/rate-limiting.md}.
 */
public final class SlidingWindowCounterRateLimiter {
    private final long maxRequests;
    private final long windowNanos;
    private final Clock clock;
    private final ConcurrentHashMap<String, WindowState> windows = new ConcurrentHashMap<>();

    public SlidingWindowCounterRateLimiter(long maxRequests, Duration windowSize, Clock clock) {
        if (maxRequests <= 0) {
            throw new IllegalArgumentException("maxRequests must be positive");
        }
        Objects.requireNonNull(windowSize, "windowSize must not be null");
        if (windowSize.isZero() || windowSize.isNegative()) {
            throw new IllegalArgumentException("windowSize must be positive");
        }
        this.maxRequests = maxRequests;
        this.windowNanos = windowSize.toNanos();
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public RateLimitDecision tryAcquire(String key) {
        Objects.requireNonNull(key, "key must not be null");
        long nowNanos = currentEpochNanos();
        long currentWindowId = Math.floorDiv(nowNanos, windowNanos);
        long elapsedInWindowNanos = Math.floorMod(nowNanos, windowNanos);
        long remainingWindowNanos = windowNanos - elapsedInWindowNanos;

        BigInteger windowBig = BigInteger.valueOf(windowNanos);
        BigInteger budgetNumerator = BigInteger.valueOf(maxRequests).multiply(windowBig);

        AtomicReference<RateLimitDecision> decisionRef = new AtomicReference<>();
        windows.compute(key, (ignored, existing) -> {
            WindowState aligned = alignWindow(existing, currentWindowId);
            BigInteger weightedPrevious = BigInteger.valueOf(aligned.previousCount)
                .multiply(BigInteger.valueOf(remainingWindowNanos));
            BigInteger candidateNumerator = weightedPrevious
                .add(BigInteger.valueOf(aligned.currentCount + 1L).multiply(windowBig));

            if (candidateNumerator.compareTo(budgetNumerator) <= 0) {
                long updatedCurrent = aligned.currentCount + 1L;
                long remaining = budgetNumerator.subtract(candidateNumerator).divide(windowBig).longValueExact();
                decisionRef.set(new RateLimitDecision(true, Math.max(0L, remaining), Duration.ZERO));
                return new WindowState(currentWindowId, aligned.previousCount, updatedCurrent);
            }
            long retryNanos = computeRetryNanos(aligned, elapsedInWindowNanos, remainingWindowNanos, windowBig);
            decisionRef.set(new RateLimitDecision(false, 0L, Duration.ofNanos(retryNanos)));
            return aligned;
        });
        return decisionRef.get();
    }

    private WindowState alignWindow(WindowState state, long currentWindowId) {
        if (state == null) {
            return new WindowState(currentWindowId, 0L, 0L);
        }
        long delta = currentWindowId - state.windowId;
        if (delta <= 0L) {
            return state;
        }
        if (delta == 1L) {
            return new WindowState(currentWindowId, state.currentCount, 0L);
        }
        return new WindowState(currentWindowId, 0L, 0L);
    }

    private long computeRetryNanos(
        WindowState state,
        long elapsedInWindowNanos,
        long remainingInWindow,
        BigInteger windowBig
    ) {
        if (state.previousCount == 0L) {
            return Math.max(1L, remainingInWindow);
        }
        long previousSlotsToShed = state.previousCount - (maxRequests - 1L - state.currentCount);
        BigInteger previousCountBig = BigInteger.valueOf(state.previousCount);
        BigInteger targetElapsed = BigInteger.valueOf(previousSlotsToShed)
            .multiply(windowBig)
            .add(previousCountBig.subtract(BigInteger.ONE))
            .divide(previousCountBig);
        long waitNanos = targetElapsed.longValueExact() - elapsedInWindowNanos;
        return Math.max(1L, Math.min(remainingInWindow, waitNanos));
    }

    private long currentEpochNanos() {
        var now = clock.instant();
        return Math.multiplyExact(now.getEpochSecond(), 1_000_000_000L) + now.getNano();
    }

    private record WindowState(long windowId, long previousCount, long currentCount) {
    }
}
