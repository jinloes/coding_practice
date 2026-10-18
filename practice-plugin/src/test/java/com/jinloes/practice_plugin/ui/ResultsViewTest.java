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
}
