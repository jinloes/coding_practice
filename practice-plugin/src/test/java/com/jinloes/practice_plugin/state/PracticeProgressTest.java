package com.jinloes.practice_plugin.state;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PracticeProgressTest {
    @Test
    void readsBackThePersistedLegacyStateInAnotherBean() {
        PracticeProgress progress = new PracticeProgress();
        PracticeProgress.Data state = progress.getState();
        state.selectedAttempts.put("pair-sum", "pair-sum-first");
        state.testSeconds = 12;
        state.suiteSeconds = 90;
        state.heapMb = 512;

        PracticeProgress.Entry recorded = progress.entry("pair-sum-first", "pair-sum");
        recorded.status = "ASSERTION_FAILED";
        recorded.checkedFingerprint = "fingerprint-1";
        recorded.checkedAt = "2025-01-02T03:04:05Z";
        recorded.details = "bad pair";
        recorded.hintsRevealed = 2;
        recorded.tests = 11;
        recorded.failures = 2;

        PracticeProgress restored = new PracticeProgress();
        restored.loadState(copyOf(progress.getState()));

        PracticeProgress.Entry entry = restored.getState().attempts.get("pair-sum-first");
        assertThat(entry).isNotNull();
        assertThat(entry.exerciseId).isEqualTo("pair-sum");
        assertThat(entry.revision).isEqualTo("1");
        assertThat(entry.status).isEqualTo("ASSERTION_FAILED");
        assertThat(entry.checkedFingerprint).isEqualTo("fingerprint-1");
        assertThat(entry.checkedAt).isEqualTo("2025-01-02T03:04:05Z");
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
    void reusesAnExistingEntryForTheSameAttempt() {
        PracticeProgress progress = new PracticeProgress();

        PracticeProgress.Entry first = progress.entry("pair-sum-first", "pair-sum");
        first.hintsRevealed = 3;

        assertThat(progress.entry("pair-sum-first", "pair-sum"))
                .as("the same attempt keeps its recorded history")
                .isSameAs(first);
        assertThat(progress.getState().attempts).hasSize(1);
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
