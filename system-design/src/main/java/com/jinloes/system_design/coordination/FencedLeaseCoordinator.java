package com.jinloes.system_design.coordination;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Lease coordinator issuing expiring leadership leases paired with strictly monotonic fencing tokens.
 *
 * <p>See {@code docs/patterns/leader-election.md}.
 */
public final class FencedLeaseCoordinator {
    public record Lease(String resourceName, String ownerId, long fencingToken, Instant expiresAt) {
        public boolean isExpiredAt(Instant now) {
            return !now.isBefore(expiresAt);
        }
    }

    private final Clock clock;
    private final Map<String, Lease> activeLeases = new HashMap<>();
    private long nextFencingToken;

    public FencedLeaseCoordinator(Clock clock) {
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    /**
     * Acquires a new lease if none is active or the prior lease has expired, or renews an active
     * lease already owned by {@code ownerId}. Every new leadership epoch receives a higher token.
     */
    public synchronized Optional<Lease> tryAcquireOrRenew(String resourceName, String ownerId, Duration ttl) {
        validateInputs(resourceName, ownerId, ttl);
        Instant now = clock.instant();
        Lease current = activeLeases.get(resourceName);

        if (current != null && !current.isExpiredAt(now)) {
            if (!current.ownerId().equals(ownerId)) {
                return Optional.empty();
            }
            Lease renewed = new Lease(resourceName, ownerId, current.fencingToken(), now.plus(ttl));
            activeLeases.put(resourceName, renewed);
            return Optional.of(renewed);
        }

        long issuedToken = ++nextFencingToken;
        Lease acquired = new Lease(resourceName, ownerId, issuedToken, now.plus(ttl));
        activeLeases.put(resourceName, acquired);
        return Optional.of(acquired);
    }

    public synchronized boolean release(String resourceName, String ownerId, long fencingToken) {
        Objects.requireNonNull(resourceName, "resourceName must not be null");
        Objects.requireNonNull(ownerId, "ownerId must not be null");
        Lease current = activeLeases.get(resourceName);
        if (current == null || !current.ownerId().equals(ownerId) || current.fencingToken() != fencingToken) {
            return false;
        }
        activeLeases.remove(resourceName);
        return true;
    }

    private static void validateInputs(String resourceName, String ownerId, Duration ttl) {
        Objects.requireNonNull(resourceName, "resourceName must not be null");
        Objects.requireNonNull(ownerId, "ownerId must not be null");
        Objects.requireNonNull(ttl, "ttl must not be null");
        if (ttl.isZero() || ttl.isNegative()) {
            throw new IllegalArgumentException("ttl must be positive");
        }
    }
}
