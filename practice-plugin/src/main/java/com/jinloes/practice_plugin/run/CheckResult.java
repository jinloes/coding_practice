package com.jinloes.practice_plugin.run;

public record CheckResult(Status status, int tests, int failures, String details) {
    public enum Status {
        NOT_RUN, RUNNING, PASSED, ASSERTION_FAILED, COMPILATION_FAILED,
        RUNTIME_ERROR, TIMED_OUT, CANCELLED, RUNNER_ERROR
    }
}
