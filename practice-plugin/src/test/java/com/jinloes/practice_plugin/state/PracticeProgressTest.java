package com.jinloes.practice_plugin.state;

import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PracticeProgressTest {
    @Test
    void recordsResultsAndLoadsThePersistedStateIntoAnotherBean() {
        PracticeProgress progress = new PracticeProgress();
        PracticeProgress.Data state = progress.getState();
        state.selectedAttempts.put("pair-sum", "pair-sum-first");
        state.testSeconds = 12;
        state.suiteSeconds = 90;
        state.heapMb = 512;

        progress.record(
                "pair-sum-first",
                "pair-sum",
                "fingerprint-1",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 11, 2, "bad pair"));
        state.attempts.get("pair-sum-first").hintsRevealed = 2;

        PracticeProgress restored = new PracticeProgress();
        restored.loadState(copyOf(progress.getState()));

        PracticeProgress.Entry entry = restored.getState().attempts.get("pair-sum-first");
        assertThat(entry).isNotNull();
        assertThat(entry.exerciseId).isEqualTo("pair-sum");
        assertThat(entry.revision).isEqualTo("1");
        assertThat(entry.status).isEqualTo("ASSERTION_FAILED");
        assertThat(entry.checkedFingerprint).isEqualTo("fingerprint-1");
        assertThat(entry.checkedAt).isNotBlank();
        assertThat(entry.details).isEqualTo("bad pair");
        assertThat(entry.hintsRevealed).isEqualTo(2);
        assertThat(entry.tests).isEqualTo(11);
        assertThat(entry.failures).isEqualTo(2);
        assertThat(restored.getState().selectedAttempts)
                .containsEntry("pair-sum", "pair-sum-first");
        assertThat(restored.getState().testSeconds).isEqualTo(12);
        assertThat(restored.getState().suiteSeconds).isEqualTo(90);
        assertThat(restored.getState().heapMb).isEqualTo(512);
    }

    @Test
    void retainsTheMostRecentHistoricalPassWhenALaterCheckFails() {
        PracticeProgress progress = new PracticeProgress();

        progress.record(
                "pair-sum-first",
                "pair-sum",
                "passing-fingerprint",
                new CheckResult(CheckResult.Status.PASSED, 11, 0, "passed"));
        String lastPassedAt = progress.getState().attempts.get("pair-sum-first").lastPassedAt;

        progress.record(
                "pair-sum-first",
                "pair-sum",
                "failing-fingerprint",
                new CheckResult(CheckResult.Status.RUNTIME_ERROR, 11, 1, "runtime error"));

        PracticeProgress.Entry entry = progress.getState().attempts.get("pair-sum-first");
        assertThat(lastPassedAt).isNotBlank();
        assertThat(entry.status).isEqualTo("RUNTIME_ERROR");
        assertThat(entry.checkedFingerprint).isEqualTo("failing-fingerprint");
        assertThat(entry.lastPassedAt).isEqualTo(lastPassedAt);
    }

    @Test
    void acceptsTheDocumentedLimitBoundaries() {
        PracticeProgress progress = new PracticeProgress();
        PracticeProgress.Data state = progress.getState();

        state.testSeconds = 1;
        state.suiteSeconds = 1;
        state.heapMb = 64;
        progress.validateLimits();

        state.testSeconds = 300;
        state.suiteSeconds = 1800;
        state.heapMb = 2048;
        progress.validateLimits();
    }

    @Test
    void rejectsEveryLimitOutsideItsDocumentedRange() {
        assertInvalid(0, 5, 256);
        assertInvalid(301, 301, 256);
        assertInvalid(5, 4, 256);
        assertInvalid(5, 1801, 256);
        assertInvalid(5, 60, 63);
        assertInvalid(5, 60, 2049);
    }

    private static void assertInvalid(int testSeconds, int suiteSeconds, int heapMb) {
        PracticeProgress progress = new PracticeProgress();
        PracticeProgress.Data state = progress.getState();
        state.testSeconds = testSeconds;
        state.suiteSeconds = suiteSeconds;
        state.heapMb = heapMb;

        assertThatThrownBy(progress::validateLimits)
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static PracticeProgress.Data copyOf(PracticeProgress.Data source) {
        PracticeProgress.Data copy = new PracticeProgress.Data();
        copy.selectedAttempts.putAll(source.selectedAttempts);
        copy.testSeconds = source.testSeconds;
        copy.suiteSeconds = source.suiteSeconds;
        copy.heapMb = source.heapMb;
        source.attempts.forEach((attemptId, sourceEntry) -> {
            PracticeProgress.Entry entry = new PracticeProgress.Entry();
            entry.exerciseId = sourceEntry.exerciseId;
            entry.revision = sourceEntry.revision;
            entry.status = sourceEntry.status;
            entry.checkedFingerprint = sourceEntry.checkedFingerprint;
            entry.checkedAt = sourceEntry.checkedAt;
            entry.lastPassedAt = sourceEntry.lastPassedAt;
            entry.details = sourceEntry.details;
            entry.hintsRevealed = sourceEntry.hintsRevealed;
            entry.tests = sourceEntry.tests;
            entry.failures = sourceEntry.failures;
            copy.attempts.put(attemptId, entry);
        });
        return copy;
    }
}
