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
    void validatesAttemptOwnershipAndExecutionLimits() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        progress.entry("pair-sum-a", "pair-sum");
        assertThatThrownBy(() -> progress.entry("pair-sum-a", "binary-search"))
                .isInstanceOf(IllegalArgumentException.class);
        progress.getState().heapMb = 63;
        assertThatThrownBy(progress::validateLimits).isInstanceOf(IllegalArgumentException.class);
    }
}
