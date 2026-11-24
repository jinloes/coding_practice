package com.jinloes.practice_plugin.ui;

import com.jinloes.practice_plugin.ui.ResultsView.RunBanner;
import com.jinloes.practice_plugin.ui.ResultsView.RunOutcome;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ResultsViewTest {
    private static final String SUFFIX = "). Use Check Solution to update correctness progress.";

    @Test
    void passingExamplesSayTheyPassed() {
        assertThat(ResultsView.runBanner("Example session finished (exit 0" + SUFFIX))
                .isEqualTo(new RunBanner(RunOutcome.PASSED, "Examples passed"));
    }

    @Test
    void failingExamplesReportTheirExitCodeNeutrally() {
        assertThat(ResultsView.runBanner("Example session finished (exit 1" + SUFFIX))
                .isEqualTo(new RunBanner(RunOutcome.FAILED,
                        "Examples did not finish successfully (exit 1); see the Run console"));
        assertThat(ResultsView.runBanner("Example session finished (exit -1" + SUFFIX).text())
                .as("a negative exit code, such as a stopped debug session")
                .isEqualTo("Examples did not finish successfully (exit -1); see the Run console");
    }

    @Test
    void finishedInputRunsPointToTheRunConsoleWithoutClaimingAValue() {
        assertThat(ResultsView.runBanner("Input session finished (exit 0" + SUFFIX))
                .isEqualTo(new RunBanner(RunOutcome.INFO, "Input run finished; see the Run console for the output"));
    }

    @Test
    void failedInputRunsReportTheirExitCode() {
        assertThat(ResultsView.runBanner("Input session finished (exit 3" + SUFFIX))
                .isEqualTo(new RunBanner(RunOutcome.ERROR, "Input run did not complete (exit 3); see the Run console"));
    }

    @Test
    void staleResultsSayTheyAreStale() {
        assertThat(ResultsView.runBanner(
                "STALE: files changed during the example run; run the current solution again."))
                .isEqualTo(new RunBanner(RunOutcome.STALE, "Example result is stale"));
    }

    @Test
    void compileAndLaunchFailuresSayTheExamplesCouldNotRun() {
        RunBanner couldNotRun = new RunBanner(RunOutcome.ERROR, "Examples could not run; see details below");
        assertThat(ResultsView.runBanner("Example compilation failed:\nSolution.java:3: error")).isEqualTo(couldNotRun);
        assertThat(ResultsView.runBanner("Example compilation exceeded 60 seconds.")).isEqualTo(couldNotRun);
        assertThat(ResultsView.runBanner("Example launch failed before execution: x")).isEqualTo(couldNotRun);
        assertThat(ResultsView.runBanner("Generated example runner is not owned by the attempt module."))
                .isEqualTo(couldNotRun);
    }

    @Test
    void anythingElseKeepsTheGenericBanner() {
        RunBanner generic = new RunBanner(RunOutcome.INFO, "Run finished");
        assertThat(ResultsView.runBanner("Example result is unavailable: disk error")).isEqualTo(generic);
        assertThat(ResultsView.runBanner("")).isEqualTo(generic);
        assertThat(ResultsView.runBanner(null)).isEqualTo(generic);
    }

    @Test
    void markStaleIsIdempotentAndResetsWhenANewResultArrives() {
        ResultsView view = new ResultsView();
        var entry = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        entry.status = "PASSED";
        entry.checkedFingerprint = "abc";
        entry.details = "All 10 supplied cases passed.";

        view.showEntry(entry);
        assertThat(view.hasDisplayedResult()).isTrue();
        assertThat(view.bannerText()).isEqualTo("Passed");

        view.markStale("edited");
        view.markStale("edited again");

        assertThat(view.bannerText()).isEqualTo("Passed (stale)");
        assertThat(view.outputText()).isEqualTo("All 10 supplied cases passed.\nSTALE: edited");

        view.clear();
        assertThat(view.hasDisplayedResult()).isFalse();
        view.markStale("orphan");
        assertThat(view.bannerText()).isEqualTo("Stale");
        assertThat(view.outputText()).isEqualTo("STALE: orphan");
    }

    @Test
    void showEntryAppendsComplexityOnlyWhenCurrentStatusIsPassed() {
        ResultsView view = new ResultsView();
        var passed = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        passed.status = "PASSED";
        passed.complexity = "Scaling: O(n) (intended O(n))";
        passed.details = "All 10 supplied cases passed.";

        view.showEntry(passed);
        assertThat(view.bannerText()).isEqualTo("Passed");
        assertThat(view.outputText())
                .isEqualTo("All 10 supplied cases passed.\n\nScaling: O(n) (intended O(n))");

        var regressed = new com.jinloes.practice_plugin.state.ManagedPracticeProgress.Entry();
        regressed.status = "ASSERTION_FAILED";
        regressed.complexity = "Scaling: O(n) (intended O(n))";
        regressed.details = "1 case failed.";

        view.showEntry(regressed);
        assertThat(view.bannerText()).isEqualTo("Failed: wrong answer");
        assertThat(view.outputText())
                .as("prior scaling output must not appear on non-passing results")
                .isEqualTo("1 case failed.");
    }

    @Test
    void askCoachStateUpdatesRenderedResultsPage() {
        var rendered = new java.util.concurrent.atomic.AtomicReference<PracticePage.Results>();
        PageView fakeView = new PageView() {
            @Override public javax.swing.JComponent component() { return new javax.swing.JPanel(); }
            @Override public void show(PracticePage page) { rendered.set((PracticePage.Results) page); }
        };
        ResultsView view = new ResultsView(fakeView);

        assertThat(view.isAskCoachEnabled()).isFalse();
        assertThat(rendered.get().askCoachEnabled()).isFalse();

        view.setAskCoachEnabled(false);
        assertThat(view.isAskCoachEnabled()).isFalse();

        view.setAskCoachEnabled(true);
        assertThat(view.isAskCoachEnabled()).isTrue();
        assertThat(rendered.get().askCoachEnabled()).isTrue();
    }

    @Test
    void coachControllerEnforcesUnlockPolicyRejectsLockedClearAndIsolatesConcurrentReplies() {
        var pairSum = com.jinloes.practice_plugin.catalog.ExerciseCatalog.find("pair-sum");
        var binarySearch = com.jinloes.practice_plugin.catalog.ExerciseCatalog.find("binary-search");
        var attemptA = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt(
                "pair-sum-a", pairSum.id(), "v1", java.nio.file.Path.of("a/Solution.java"));
        var attemptB = new com.jinloes.practice_plugin.workspace.ManagedPracticeWorkspace.Attempt(
                "binary-search-b", binarySearch.id(), "v1", java.nio.file.Path.of("b/Solution.java"));

        var progress = new com.jinloes.practice_plugin.state.ManagedPracticeProgress();
        var currentExercise = new java.util.concurrent.atomic.AtomicReference<>(pairSum);
        var currentAttempt = new java.util.concurrent.atomic.AtomicReference<>(attemptA);
        var runnerRunning = new java.util.concurrent.atomic.AtomicBoolean(false);
        var rendered = new java.util.concurrent.atomic.AtomicReference<PracticePage.Coach>();
        PageView fakeView = new PageView() {
            @Override public javax.swing.JComponent component() { return new javax.swing.JPanel(); }
            @Override public void show(PracticePage page) { rendered.set((PracticePage.Coach) page); }
        };
        var pendingBackground = new java.util.ArrayDeque<Runnable>();
        CoachController controller = new CoachController(
                null, progress, fakeView, currentExercise::get, currentAttempt::get,
                java.util.Set::of, runnerRunning::get, () -> true,
                (prompt, settings, canceled) -> "## Coach Reply\nUse a table.",
                ignored -> "class Solution {}", pendingBackground::addLast, Runnable::run);

        assertThat(controller.isUnlocked()).isFalse();
        controller.ask(com.jinloes.practice_plugin.coach.CoachPrompt.Mode.DIAGNOSE, "");
        assertThat(pendingBackground).isEmpty();
        assertThat(rendered.get().unlocked()).isFalse();

        progress.record(attemptA.id(), pairSum.id(), "fp-cancel",
                new com.jinloes.practice_plugin.run.CheckResult(
                        com.jinloes.practice_plugin.run.CheckResult.Status.CANCELLED, 0, 0, "cancelled"));
        assertThat(controller.isUnlocked()).isFalse();
        progress.record(attemptA.id(), pairSum.id(), "fp-err",
                new com.jinloes.practice_plugin.run.CheckResult(
                        com.jinloes.practice_plugin.run.CheckResult.Status.RUNNER_ERROR, 0, 0, "runner error"));
        assertThat(controller.isUnlocked()).isFalse();

        progress.record(attemptA.id(), pairSum.id(), "fp-fail",
                new com.jinloes.practice_plugin.run.CheckResult(
                        com.jinloes.practice_plugin.run.CheckResult.Status.ASSERTION_FAILED, 5, 1, "wrong answer"));
        progress.record(attemptB.id(), binarySearch.id(), "fp-pass",
                new com.jinloes.practice_plugin.run.CheckResult(
                        com.jinloes.practice_plugin.run.CheckResult.Status.PASSED, 5, 0, "all passed"));
        assertThat(controller.isUnlocked()).isTrue();

        // Switch attempts mid-flight; reply binds to attemptA and does not leak to attemptB.
        controller.ask(com.jinloes.practice_plugin.coach.CoachPrompt.Mode.TRANSLATE, "");
        assertThat(pendingBackground).hasSize(1);
        currentExercise.set(binarySearch);
        currentAttempt.set(attemptB);
        controller.refresh();
        pendingBackground.removeFirst().run();
        assertThat(progress.coachHistory(attemptA.id(), pairSum.id())).hasSize(2);
        assertThat(progress.coachHistory(attemptB.id(), binarySearch.id())).isEmpty();

        // Locked clear with saved history (e.g. while a re-check is running) preserves history.
        currentExercise.set(pairSum);
        currentAttempt.set(attemptA);
        runnerRunning.set(true);
        assertThat(controller.isUnlocked()).isFalse();
        controller.clear();
        assertThat(progress.coachHistory(attemptA.id(), pairSum.id())).hasSize(2);
        runnerRunning.set(false);
        assertThat(controller.isUnlocked()).isTrue();

        // Unlocked clear and cancel invalidate in-flight replies.
        currentExercise.set(binarySearch);
        currentAttempt.set(attemptB);
        controller.ask(com.jinloes.practice_plugin.coach.CoachPrompt.Mode.COMPARE_OPTIMAL, "");
        controller.clear();
        pendingBackground.removeFirst().run();
        assertThat(progress.coachHistory(attemptB.id(), binarySearch.id())).isEmpty();

        controller.ask(com.jinloes.practice_plugin.coach.CoachPrompt.Mode.FOLLOW_UP, "Why?");
        controller.cancel();
        pendingBackground.removeFirst().run();
        assertThat(progress.coachHistory(attemptB.id(), binarySearch.id())).isEmpty();
    }
}
