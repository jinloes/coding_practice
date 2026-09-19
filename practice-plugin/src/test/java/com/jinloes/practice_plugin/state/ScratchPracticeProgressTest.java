package com.jinloes.practice_plugin.state;

import com.jinloes.practice_plugin.run.CheckResult;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScratchPracticeProgressTest {
    @Test
    void stateRoundTripsSelectionsLimitsAndHistoricalPasses() {
        ScratchPracticeProgress original = new ScratchPracticeProgress();
        var entry = original.entry("pair-sum-0123456789abcdef0123456789abcdef", "pair-sum");
        entry.hintsRevealed = 2;
        original.getState().selectedAttempts.put("pair-sum", "pair-sum-0123456789abcdef0123456789abcdef");
        original.getState().testSeconds = 7;
        original.getState().suiteSeconds = 70;
        original.getState().heapMb = 512;
        original.record(entry.exerciseId + "-0123456789abcdef0123456789abcdef", "pair-sum", "first",
                new CheckResult(CheckResult.Status.PASSED, 11, 0, "passed"));

        ScratchPracticeProgress restored = new ScratchPracticeProgress();
        restored.loadState(original.getState());
        restored.record(entry.exerciseId + "-0123456789abcdef0123456789abcdef", "pair-sum", "second",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 11, 1, "failed"));

        var restoredEntry = restored.entry("pair-sum-0123456789abcdef0123456789abcdef", "pair-sum");
        assertThat(restored.getState().selectedAttempts).containsEntry("pair-sum", entry.exerciseId
                + "-0123456789abcdef0123456789abcdef");
        assertThat(restored.getState().testSeconds).isEqualTo(7);
        assertThat(restored.getState().suiteSeconds).isEqualTo(70);
        assertThat(restored.getState().heapMb).isEqualTo(512);
        assertThat(restoredEntry.hintsRevealed).isEqualTo(2);
        assertThat(restoredEntry.status).isEqualTo("ASSERTION_FAILED");
        assertThat(restoredEntry.lastPassedAt).isNotBlank();
        assertThat(restoredEntry.checkedFingerprint).isEqualTo("second");
    }
}
