package com.jinloes.practice_plugin.run;

import java.util.Objects;

public record CheckResult(Status status, int tests, int failures, String details, String complexity) {
    public enum Status {
        NOT_RUN, RUNNING, PASSED, ASSERTION_FAILED, COMPILATION_FAILED,
        RUNTIME_ERROR, TIMED_OUT, CANCELLED, RUNNER_ERROR
    }

    public CheckResult {
        details = Objects.requireNonNullElse(details, "");
        complexity = Objects.requireNonNullElse(complexity, "");
    }

    public CheckResult(Status status, int tests, int failures, String details) {
        this(status, tests, failures, details, "");
    }

    public CheckResult withComplexity(String measured) {
        return new CheckResult(status, tests, failures, details, measured);
    }
}
