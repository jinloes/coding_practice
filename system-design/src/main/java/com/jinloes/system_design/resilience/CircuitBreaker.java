package com.jinloes.system_design.resilience;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * State-machine circuit breaker ({@code CLOSED -> OPEN -> HALF_OPEN}) that fails fast while a
 * downstream dependency is unhealthy and bounds recovery probe concurrency.
 * Outcomes update health only in the state generation that granted their permission.
 *
 * <p>See {@code docs/patterns/resilience.md}.
 */
public final class CircuitBreaker {
    public enum State {
        CLOSED,
        OPEN,
        HALF_OPEN
    }

    public static final class CallNotPermittedException extends RuntimeException {
        public CallNotPermittedException(State state) {
            super("Circuit breaker is " + state + " and rejected call");
        }
    }

    private final int failureThreshold;
    private final Duration openDuration;
    private final int halfOpenMaxProbes;
    private final Clock clock;

    private State state = State.CLOSED;
    private long generation;
    private int consecutiveFailures;
    private Instant openedAt;
    private int halfOpenInFlightOrUsedProbes;
    private int halfOpenSuccesses;

    public CircuitBreaker(int failureThreshold, Duration openDuration, int halfOpenMaxProbes, Clock clock) {
        if (failureThreshold <= 0) {
            throw new IllegalArgumentException("failureThreshold must be positive");
        }
        Objects.requireNonNull(openDuration, "openDuration must not be null");
        if (openDuration.isZero() || openDuration.isNegative()) {
            throw new IllegalArgumentException("openDuration must be positive");
        }
        if (halfOpenMaxProbes <= 0) {
            throw new IllegalArgumentException("halfOpenMaxProbes must be positive");
        }
        this.failureThreshold = failureThreshold;
        this.openDuration = openDuration;
        this.halfOpenMaxProbes = halfOpenMaxProbes;
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public synchronized State state() {
        advanceIfOpenExpired();
        return state;
    }

    public <T> T execute(Supplier<T> call) {
        Objects.requireNonNull(call, "call must not be null");
        Permission permission = acquirePermission();
        try {
            T result = call.get();
            onSuccess(permission);
            return result;
        } catch (RuntimeException | Error ex) {
            onFailure(permission);
            throw ex;
        }
    }

    private record Permission(State state, long generation) {
    }

    private synchronized Permission acquirePermission() {
        advanceIfOpenExpired();
        if (state == State.OPEN) {
            throw new CallNotPermittedException(State.OPEN);
        }
        if (state == State.HALF_OPEN) {
            if (halfOpenInFlightOrUsedProbes >= halfOpenMaxProbes) {
                throw new CallNotPermittedException(State.HALF_OPEN);
            }
            halfOpenInFlightOrUsedProbes++;
        }
        return new Permission(state, generation);
    }

    private synchronized void onSuccess(Permission permission) {
        if (permission.generation() != generation) {
            return;
        }
        if (permission.state() == State.HALF_OPEN) {
            halfOpenSuccesses++;
            if (halfOpenSuccesses >= halfOpenMaxProbes) {
                transitionToClosed();
            }
        } else {
            consecutiveFailures = 0;
        }
    }

    private synchronized void onFailure(Permission permission) {
        if (permission.generation() != generation) {
            return;
        }
        if (permission.state() == State.HALF_OPEN) {
            tripOpen();
            return;
        }
        consecutiveFailures++;
        if (consecutiveFailures >= failureThreshold) {
            tripOpen();
        }
    }

    private void advanceIfOpenExpired() {
        if (state == State.OPEN && !clock.instant().isBefore(openedAt.plus(openDuration))) {
            state = State.HALF_OPEN;
            generation++;
            halfOpenInFlightOrUsedProbes = 0;
            halfOpenSuccesses = 0;
        }
    }

    private void tripOpen() {
        state = State.OPEN;
        generation++;
        openedAt = clock.instant();
        consecutiveFailures = 0;
        halfOpenInFlightOrUsedProbes = 0;
        halfOpenSuccesses = 0;
    }

    private void transitionToClosed() {
        state = State.CLOSED;
        generation++;
        consecutiveFailures = 0;
        openedAt = null;
        halfOpenInFlightOrUsedProbes = 0;
        halfOpenSuccesses = 0;
    }
}
