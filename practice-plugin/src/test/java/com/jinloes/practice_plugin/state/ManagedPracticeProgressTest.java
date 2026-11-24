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
    void loadStateBackfillsCollectionsForOlderPersistentState() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        ManagedPracticeProgress.Data state = new ManagedPracticeProgress.Data();
        state.attempts = null;
        state.selectedAttempts = null;
        state.legacyMappings = null;

        progress.loadState(state);

        assertThat(progress.getState().attempts).isEmpty();
        assertThat(progress.getState().selectedAttempts).isEmpty();
        assertThat(progress.getState().legacyMappings).isEmpty();
        progress.recordImport("/legacy", "attempt", "managed");
        assertThat(progress.importedAttempt("/legacy", "attempt")).isEqualTo("managed");
    }

    @Test
    void recordsNonBlankComplexityAndKeepsBlankUpdatesFromErasingIt() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        String attemptId = "pair-sum-0123456789abcdef0123456789abcdef";

        progress.record(attemptId, "pair-sum", "first",
                new CheckResult(CheckResult.Status.PASSED, 3, 0, "passed", "O(n)"));
        progress.record(attemptId, "pair-sum", "second",
                new CheckResult(CheckResult.Status.ASSERTION_FAILED, 3, 1, "failed", ""));

        ManagedPracticeProgress.Entry entry = progress.entry(attemptId, "pair-sum");
        assertThat(entry.complexity).isEqualTo("O(n)");
        assertThat(entry.status).isEqualTo("ASSERTION_FAILED");
    }

    @Test
    void detectsDefaultLimitsAcrossAllThreeFields() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        assertThat(progress.hasDefaultLimits()).isTrue();

        progress.getState().testSeconds = 6;
        assertThat(progress.hasDefaultLimits()).isFalse();

        progress.getState().testSeconds = ManagedPracticeProgress.DEFAULT_TEST_SECONDS;
        progress.getState().suiteSeconds = 61;
        assertThat(progress.hasDefaultLimits()).isFalse();

        progress.getState().suiteSeconds = ManagedPracticeProgress.DEFAULT_SUITE_SECONDS;
        progress.getState().heapMb = 512;
        assertThat(progress.hasDefaultLimits()).isFalse();
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

    @Test
    void persistsAndCapsCoachSettingsAndPerAttemptHistory() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        progress.getState().coachCliPath = "/usr/local/bin/copilot";
        progress.getState().coachModel = "gpt-5";
        progress.getState().coachTimeoutSeconds = 90;

        String attemptA = "pair-sum-a";
        String attemptB = "pair-sum-b";
        String huge = "Z".repeat(ManagedPracticeProgress.MAX_COACH_TURN_CHARS + 200);
        progress.appendCoachTurn(attemptA, "pair-sum", "user", huge);
        for (int i = 1; i <= ManagedPracticeProgress.MAX_COACH_TURNS + 2; i++) {
            progress.appendCoachTurn(attemptA, "pair-sum", i % 2 == 0 ? "user" : "assistant", "turn-" + i);
        }
        progress.appendCoachTurn(attemptB, "pair-sum", "user", "keep-me");

        assertThat(progress.coachHistory(attemptA, "pair-sum")).hasSize(ManagedPracticeProgress.MAX_COACH_TURNS);
        assertThat(progress.coachHistory(attemptA, "pair-sum").get(0).text).isEqualTo("turn-3");
        assertThat(progress.coachHistory(attemptA, "pair-sum").get(ManagedPracticeProgress.MAX_COACH_TURNS - 1).text)
                .isEqualTo("turn-22");

        progress.appendCoachTurn(attemptB, "pair-sum", "assistant", huge);
        assertThat(progress.coachHistory(attemptB, "pair-sum").get(1).text)
                .hasSize(ManagedPracticeProgress.MAX_COACH_TURN_CHARS);

        progress.clearCoachHistory(attemptA, "pair-sum");
        assertThat(progress.coachHistory(attemptA, "pair-sum")).isEmpty();
        assertThat(progress.coachHistory(attemptB, "pair-sum")).hasSize(2);

        assertThat(ManagedPracticeProgress.validCoachTimeout(14)).isFalse();
        assertThat(ManagedPracticeProgress.validCoachTimeout(15)).isTrue();
        assertThat(ManagedPracticeProgress.validCoachTimeout(600)).isTrue();
        assertThat(ManagedPracticeProgress.validCoachTimeout(601)).isFalse();
        assertThat(new ManagedPracticeProgress.CoachTurn().role).isEmpty();
    }

    @Test
    void loadStateNormalizesMissingCoachSettingsAndNullCoachHistory() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        ManagedPracticeProgress.Data state = new ManagedPracticeProgress.Data();
        state.coachCliPath = null;
        state.coachModel = null;
        state.coachTimeoutSeconds = 0;
        ManagedPracticeProgress.Entry legacy = new ManagedPracticeProgress.Entry();
        legacy.exerciseId = "pair-sum";
        legacy.coachHistory = null;
        state.attempts.put("pair-sum-legacy", legacy);

        ManagedPracticeProgress.Entry oversized = new ManagedPracticeProgress.Entry();
        oversized.exerciseId = "pair-sum";
        for (int i = 1; i <= ManagedPracticeProgress.MAX_COACH_TURNS + 3; i++) {
            oversized.coachHistory.add(new ManagedPracticeProgress.CoachTurn("user", "loaded-" + i, "2026-01-01T00:00:00Z"));
        }
        oversized.coachHistory.add(new ManagedPracticeProgress.CoachTurn(
                "assistant", "X".repeat(ManagedPracticeProgress.MAX_COACH_TURN_CHARS + 500), "2026-01-01T00:00:01Z"));
        ManagedPracticeProgress.CoachTurn nullFields = new ManagedPracticeProgress.CoachTurn();
        nullFields.role = null;
        nullFields.text = null;
        nullFields.createdAt = null;
        oversized.coachHistory.add(nullFields);
        state.attempts.put("pair-sum-oversized", oversized);

        progress.loadState(state);

        assertThat(progress.getState().coachCliPath).isEmpty();
        assertThat(progress.getState().coachModel).isEmpty();
        assertThat(progress.getState().coachTimeoutSeconds)
                .isEqualTo(ManagedPracticeProgress.DEFAULT_COACH_TIMEOUT_SECONDS);
        assertThat(progress.coachHistory("pair-sum-legacy", "pair-sum")).isEmpty();

        var loadedHistory = progress.coachHistory("pair-sum-oversized", "pair-sum");
        assertThat(loadedHistory).hasSize(ManagedPracticeProgress.MAX_COACH_TURNS);
        assertThat(loadedHistory.get(0).text).isEqualTo("loaded-6");
        assertThat(loadedHistory.get(ManagedPracticeProgress.MAX_COACH_TURNS - 2).text)
                .hasSize(ManagedPracticeProgress.MAX_COACH_TURN_CHARS);
        var normalizedNull = loadedHistory.get(ManagedPracticeProgress.MAX_COACH_TURNS - 1);
        assertThat(normalizedNull.role).isEmpty();
        assertThat(normalizedNull.text).isEmpty();
        assertThat(normalizedNull.createdAt).isEmpty();
    }

    @Test
    void clearsCoachHistoryOnlyWhenCompletedCheckHasChangedFingerprint() {
        ManagedPracticeProgress progress = new ManagedPracticeProgress();
        String attemptId = "pair-sum-recheck";

        progress.record(attemptId, "pair-sum", "fp-1",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "passed"));
        progress.appendCoachTurn(attemptId, "pair-sum", "user", "Compare with optimal");
        progress.appendCoachTurn(attemptId, "pair-sum", "assistant", "Old O(n^2) analysis");

        // Re-checking identical code preserves history.
        progress.record(attemptId, "pair-sum", "fp-1",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "passed"));
        assertThat(progress.coachHistory(attemptId, "pair-sum")).hasSize(2);

        // Cancelled or runner-error checks with a new fingerprint preserve history.
        progress.record(attemptId, "pair-sum", "fp-2",
                new CheckResult(CheckResult.Status.CANCELLED, 0, 0, "cancelled"));
        assertThat(progress.coachHistory(attemptId, "pair-sum")).hasSize(2);
        progress.record(attemptId, "pair-sum", "fp-3",
                new CheckResult(CheckResult.Status.RUNNER_ERROR, 0, 0, "error"));
        assertThat(progress.coachHistory(attemptId, "pair-sum")).hasSize(2);

        // A completed check with a new fingerprint clears stale history.
        progress.record(attemptId, "pair-sum", "fp-4",
                new CheckResult(CheckResult.Status.PASSED, 5, 0, "passed"));
        assertThat(progress.coachHistory(attemptId, "pair-sum")).isEmpty();
    }

    @Test
    void serializesAndDeserializesNewAndLegacyXmlState() throws Exception {
        ManagedPracticeProgress original = new ManagedPracticeProgress();
        original.record("pair-sum-1", "pair-sum", "fp1",
                new CheckResult(CheckResult.Status.PASSED, 3, 0, "ok"));
        original.getState().coachModel = "gpt-5";
        original.getState().coachTimeoutSeconds = 120;
        original.getState().coachCliPath = "/usr/local/bin/copilot";
        original.appendCoachTurn("pair-sum-1", "pair-sum", "user", "Explain DP state");
        original.appendCoachTurn("pair-sum-1", "pair-sum", "assistant", "Use dp[i].");

        org.jdom.Element xml = com.intellij.util.xmlb.XmlSerializer.serialize(original.getState());
        ManagedPracticeProgress.Data roundTripped =
                com.intellij.util.xmlb.XmlSerializer.deserialize(xml, ManagedPracticeProgress.Data.class);
        ManagedPracticeProgress reloaded = new ManagedPracticeProgress();
        reloaded.loadState(roundTripped);

        assertThat(reloaded.getState().coachModel).isEqualTo("gpt-5");
        assertThat(reloaded.getState().coachTimeoutSeconds).isEqualTo(120);
        assertThat(reloaded.getState().coachCliPath).isEqualTo("/usr/local/bin/copilot");
        assertThat(reloaded.coachHistory("pair-sum-1", "pair-sum"))
                .extracting(t -> t.role, t -> t.text)
                .containsExactly(
                        org.assertj.core.groups.Tuple.tuple("user", "Explain DP state"),
                        org.assertj.core.groups.Tuple.tuple("assistant", "Use dp[i]."));

        // Old persisted XML without coachModel, coachTimeoutSeconds, coachCliPath, or coachHistory
        String legacyXml = """
                <Data>
                  <option name="attempts">
                    <map>
                      <entry key="pair-sum-old">
                        <value>
                          <Entry>
                            <option name="exerciseId" value="pair-sum" />
                            <option name="status" value="PASSED" />
                            <option name="checkedFingerprint" value="oldfp" />
                          </Entry>
                        </value>
                      </entry>
                    </map>
                  </option>
                </Data>
                """;
        org.jdom.Element legacyElement = com.intellij.openapi.util.JDOMUtil.load(legacyXml);
        ManagedPracticeProgress.Data legacyData =
                com.intellij.util.xmlb.XmlSerializer.deserialize(legacyElement, ManagedPracticeProgress.Data.class);
        ManagedPracticeProgress fromLegacy = new ManagedPracticeProgress();
        fromLegacy.loadState(legacyData);

        assertThat(fromLegacy.getState().coachModel).isEmpty();
        assertThat(fromLegacy.getState().coachCliPath).isEmpty();
        assertThat(fromLegacy.getState().coachTimeoutSeconds)
                .isEqualTo(ManagedPracticeProgress.DEFAULT_COACH_TIMEOUT_SECONDS);
        assertThat(fromLegacy.entry("pair-sum-old", "pair-sum").status).isEqualTo("PASSED");
        assertThat(fromLegacy.coachHistory("pair-sum-old", "pair-sum")).isEmpty();
    }
}
