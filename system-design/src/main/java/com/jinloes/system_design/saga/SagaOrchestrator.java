package com.jinloes.system_design.saga;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Orchestrated saga runner that executes local steps in order and runs compensating actions in
 * reverse order when a forward step fails.
 *
 * <p>See {@code docs/patterns/saga.md}.
 */
public final class SagaOrchestrator<C> {
    public enum Status {
        COMPLETED,
        COMPENSATED,
        COMPENSATION_FAILED
    }

    public record Step<C>(String name, Consumer<C> action, Consumer<C> compensation) {
        public Step {
            Objects.requireNonNull(name, "name must not be null");
            Objects.requireNonNull(action, "action must not be null");
            Objects.requireNonNull(compensation, "compensation must not be null");
        }
    }

    public record SagaResult(
        Status status,
        List<String> completedSteps,
        List<String> compensatedSteps,
        Throwable failureCause,
        List<Throwable> compensationFailures
    ) {
    }

    private final List<Step<C>> steps;

    public SagaOrchestrator(List<Step<C>> steps) {
        Objects.requireNonNull(steps, "steps must not be null");
        if (steps.isEmpty()) {
            throw new IllegalArgumentException("steps must not be empty");
        }
        this.steps = List.copyOf(steps);
    }

    public SagaResult execute(C context) {
        Objects.requireNonNull(context, "context must not be null");
        List<Step<C>> succeeded = new ArrayList<>(steps.size());
        List<String> completedNames = new ArrayList<>(steps.size());

        for (Step<C> step : steps) {
            try {
                step.action().accept(context);
                succeeded.add(step);
                completedNames.add(step.name());
            } catch (RuntimeException forwardFailure) {
                return compensate(context, succeeded, completedNames, forwardFailure);
            }
        }
        return new SagaResult(Status.COMPLETED, List.copyOf(completedNames), List.of(), null, List.of());
    }

    private SagaResult compensate(
        C context,
        List<Step<C>> succeeded,
        List<String> completedNames,
        RuntimeException forwardFailure
    ) {
        List<String> compensatedNames = new ArrayList<>(succeeded.size());
        List<Throwable> compensationErrors = new ArrayList<>();

        for (int i = succeeded.size() - 1; i >= 0; i--) {
            Step<C> step = succeeded.get(i);
            try {
                step.compensation().accept(context);
                compensatedNames.add(step.name());
            } catch (RuntimeException compensationFailure) {
                compensationErrors.add(compensationFailure);
            }
        }

        Status finalStatus = compensationErrors.isEmpty() ? Status.COMPENSATED : Status.COMPENSATION_FAILED;
        return new SagaResult(
            finalStatus,
            List.copyOf(completedNames),
            List.copyOf(compensatedNames),
            forwardFailure,
            List.copyOf(compensationErrors)
        );
    }
}
