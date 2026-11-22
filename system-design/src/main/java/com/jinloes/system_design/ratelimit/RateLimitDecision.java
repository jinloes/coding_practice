package com.jinloes.system_design.ratelimit;

import java.time.Duration;
import java.util.Objects;

/**
 * Outcome of evaluating a rate-limit request at a point in time.
 *
 * @param allowed    whether the request is permitted to proceed
 * @param remaining  whole permits remaining immediately after this decision
 * @param retryAfter minimum wait before retrying when rejected ({@link Duration#ZERO} when allowed)
 */
public record RateLimitDecision(boolean allowed, long remaining, Duration retryAfter) {
    public RateLimitDecision {
        if (remaining < 0) {
            throw new IllegalArgumentException("remaining must be non-negative");
        }
        Objects.requireNonNull(retryAfter, "retryAfter must not be null");
        if (retryAfter.isNegative()) {
            throw new IllegalArgumentException("retryAfter must not be negative");
        }
    }
}
