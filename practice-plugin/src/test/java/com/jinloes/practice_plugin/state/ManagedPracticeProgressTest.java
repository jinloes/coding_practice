package com.jinloes.practice_plugin.state;

import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ManagedPracticeProgressTest {
    @Test
    void stateRoundTripsSelectionsLimitsHistoryAndLegacyMappings() {
        ManagedPracticeProgress original = new ManagedPracticeProgress();
        String attemptId = "pair-sum-0123456789abcdef0123456789abcdef";
        var entry = original.entry(attemptId, "pair-sum");
        entry.hintsRevealed = 2;
        original.getState().selectedAttempts.put("pair-sum", attemptId);
        original.getState().testSeconds = 7;
        original.getState().suiteSeconds = 70;
        original.getState().heapMb = 512;
        original.record(attemptId, "pair-sum", "first",
                new CheckResult(CheckResult.Status.PASSED, 11, 0, "passed"));
        original.recordImport("/legacy", "legacy-attempt", attemptId);

        ManagedPracticeProgress restored = new ManagedPracticeProgress();
        restored.loadState(original.getState());
        restored.record(attemptId, "pair-sum", "second",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 11, 1, "failed"));

        var restoredEntry = restored.entry(attemptId, "pair-sum");
        assertThat(restored.getState().selectedAttempts).containsEntry("pair-sum", attemptId);
        assertThat(restored.getState().testSeconds).isEqualTo(7);
        assertThat(restored.getState().suiteSeconds).isEqualTo(70);
        assertThat(restored.getState().heapMb).isEqualTo(512);
        assertThat(restoredEntry.hintsRevealed).isEqualTo(2);
        assertThat(restoredEntry.status).isEqualTo("ASSERTION_FAILED");
        assertThat(restoredEntry.lastPassedAt).isNotBlank();
        assertThat(restoredEntry.checkedFingerprint).isEqualTo("second");
        assertThat(restored.importedAttempt("/legacy", "legacy-attempt")).isEqualTo(attemptId);
        restored.recordImport("/legacy", "legacy-attempt", "different");
        assertThat(restored.importedAttempt("/legacy", "legacy-attempt")).isEqualTo(attemptId);
    }

    @Test
    void reportsExercisesWithAnyHistoricallyPassingAttempt() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        progress.record("pair-sum-a", "pair-sum", "f1",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 5, 1, "failed"));
        progress.record("pair-sum-b", "pair-sum", "f2",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "passed"));
        progress.record("binary-search-a", "binary-search", "f3",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "passed"));
        progress.record("binary-search-a", "binary-search", "f4",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 5, 1, "failed"));
        progress.record("move-zeroes-a", "move-zeroes", "f5",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 5, 1, "failed"));
        progress.entry("valid-anagram-a", "valid-anagram");

        assertThat(progress.passedExerciseIds())
                .as("a later failure keeps the historical pass; never-passed exercises are excluded")
                .containsExactlyInAnyOrder("pair-sum", "binary-search");
    }

    @Test
    void validatesAttemptOwnershipAndExecutionLimits() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        progress.entry("pair-sum-a", "pair-sum");
        assertThatThrownBy(() -> progress.entry("pair-sum-a", "binary-search"))
                .isInstanceOf(IllegalArgumentException.class);
        progress.getState().heapMb = 63;
        assertThatThrownBy(progress::validateLimits).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsTheDocumentedLimitBoundaries() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        ManagedPracticeProgress.Data state = progress.getState();

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
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        ManagedPracticeProgress.Data state = progress.getState();
        state.testSeconds = testSeconds;
        state.suiteSeconds = suiteSeconds;
        state.heapMb = heapMb;

        assertThatThrownBy(progress::validateLimits)
                .as("limits %d/%d/%d are rejected", testSeconds, suiteSeconds, heapMb)
                .isInstanceOf(IllegalArgumentException.class);
    }
}
