package com.jinloes.system_design.coordination;

import java.util.Objects;

/**
 * Target storage/resource enforcing monotonic fencing tokens so a paused zombie leader cannot
 * overwrite newer state after losing its lease.
 *
 * <p>See {@code docs/patterns/leader-election.md}.
 */
public final class FencedResource<T> {
    public static final class StaleFencingTokenException extends RuntimeException {
        public StaleFencingTokenException(long attemptedToken, long highestSeenToken) {
            super("Rejected stale fencing token " + attemptedToken
                + " (highest seen token is " + highestSeenToken + ")");
        }
    }

    private long highestSeenToken;
    private T value;

    public FencedResource(T initialValue) {
        this.value = initialValue;
    }

    public synchronized void write(long fencingToken, T newValue) {
        if (fencingToken <= 0L) {
            throw new IllegalArgumentException("fencingToken must be positive");
        }
        Objects.requireNonNull(newValue, "newValue must not be null");
        if (fencingToken < highestSeenToken) {
            throw new StaleFencingTokenException(fencingToken, highestSeenToken);
        }
        highestSeenToken = fencingToken;
        value = newValue;
    }

    public synchronized T read() {
        return value;
    }

    public synchronized long highestSeenToken() {
        return highestSeenToken;
    }
}
