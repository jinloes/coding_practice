package com.jinloes.practice_plugin.run;

import com.jinloes.practice_plugin.catalog.ExerciseCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.jinloes.practice_plugin.run.CheckResult.Status.ASSERTION_FAILED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.PASSED;
import static com.jinloes.practice_plugin.run.CheckResult.Status.RUNNER_ERROR;
import static com.jinloes.practice_plugin.run.CheckResult.Status.RUNTIME_ERROR;
import static com.jinloes.practice_plugin.run.CheckResult.Status.TIMED_OUT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TestReportsTest {
    private static final String EXAMPLES = "com.jinloes.practice.ExamplesTest";
    private static final String CORRECTNESS = "com.jinloes.practice.CorrectnessTest";
    private static final ExerciseCatalog.Exercise PAIR_SUM = ExerciseCatalog.find("pair-sum");

    @TempDir
    Path tempDir;

    @Test
    void acceptsAFullReportUsingTheCatalogCountAndExpectedClasses() throws Exception {
        Path reports = reportDirectory();
        writeFullReport(reports, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(PASSED);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("All " + PAIR_SUM.fullCount() + " supplied cases passed");
    }

    @Test
    void classifiesAssertionFailuresAndPreservesDetailedMessages() throws Exception {
        Path reports = reportDirectory();
        String failure = caseXml(
                "findsTypicalPair",
                "<failure type=\"org.opentest4j.AssertionFailedError\" message=\"expected pair\">"
                        + "at com.jinloes.practice.ExamplesTest.findsTypicalPair(ExamplesTest.java:12)"
                        + "</failure>");
        writeFullReport(reports, failure + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(ASSERTION_FAILED);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isEqualTo(1);
        assertThat(result.details())
                .contains("Visible example: finds typical pair", "expected pair", "ExamplesTest.java:12");
    }

    @Test
    void classifiesRuntimeErrorsAndPreservesDetailedMessages() throws Exception {
        Path reports = reportDirectory();
        String error = caseXml(
                "failsAtRuntime",
                "<error type=\"java.lang.IllegalStateException\" message=\"broken setup\">"
                        + "java.lang.IllegalStateException: broken setup"
                        + "</error>");
        writeFullReport(reports, error + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(RUNTIME_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isEqualTo(1);
        assertThat(result.details()).contains("Visible example: fails at runtime", "broken setup");
    }

    @Test
    void classifiesAWrappedCrashAsARuntimeErrorRatherThanAWrongAnswer() throws Exception {
        Path reports = reportDirectory();
        String failure = caseXml(
                "findsTypicalPair",
                "<failure type=\"java.lang.AssertionError\" "
                        + "message=\"findPair([1], 2) threw java.util.EmptyStackException\">"
                        + "java.lang.AssertionError: findPair([1], 2) threw java.util.EmptyStackException\n"
                        + "Caused by: java.util.EmptyStackException\n"
                        + "\tat com.jinloes.practice.Solution.findPair(Solution.java:9)"
                        + "</failure>");
        writeFullReport(reports, failure + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status())
                .as("reporting the input must not disguise a crash as a wrong answer")
                .isEqualTo(RUNTIME_ERROR);
        assertThat(result.details())
                .contains("findPair([1], 2) threw java.util.EmptyStackException",
                        "at Solution.findPair(Solution.java:9)");
    }

    @Test
    void classifiesTimeoutFailures() throws Exception {
        Path reports = reportDirectory();
        String timeout = caseXml(
                "timesOut",
                "<error type=\"java.util.concurrent.TimeoutException\" message=\"5 seconds\">"
                        + "test exceeded its deadline"
                        + "</error>");
        writeFullReport(reports, timeout + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(TIMED_OUT);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isEqualTo(1);
    }

    @Test
    void rejectsAReportContainingSkippedTests() throws Exception {
        Path reports = reportDirectory();
        String skipped = caseXml("skippedCase", "<skipped/>");
        writeFullReport(reports, skipped + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("Incomplete test run", "skipped 1");
    }

    @Test
    void rejectsAReportMissingFromTheExpectedDirectory() throws Exception {
        CheckResult result = TestReports.read(tempDir.resolve("missing-reports"), PAIR_SUM.fullCount(), 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isZero();
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("No fresh test report");
    }

    @Test
    void rejectsDuplicateSuiteClasses() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-first.xml", EXAMPLES, passingCases(1));
        writeSuite(reports, "TEST-second.xml", EXAMPLES, passingCases(1));

        assertThatThrownBy(() -> TestReports.read(reports, 2, 0))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsReportsWithTheWrongTestCount() throws Exception {
        Path reports = reportDirectory();
        writeFullReport(reports, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount() + 1, 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("Incomplete test run");
    }

    @Test
    void rejectsAFullReportMissingAnExpectedSuiteClass() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.details()).contains(CORRECTNESS);
    }

    @Test
    void rejectsAValidReportWhenGradleExitsNonzero() throws Exception {
        Path reports = reportDirectory();
        writeFullReport(reports, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), 7);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("code 7");
    }

    @Test
    void rejectsDoctypeEntitiesBeforeReadingTheReport() throws Exception {
        Path reports = reportDirectory();
        writeRaw(reports, "TEST-Examples.xml", """
                <!DOCTYPE testsuite [
                  <!ENTITY injected "must not be expanded">
                ]>
                <testsuite name="com.jinloes.practice.ExamplesTest">
                  <testcase name="one">&injected;</testcase>
                </testsuite>
                """);
        assertThatThrownBy(() -> TestReports.read(reports, 1, 0)).isInstanceOf(IOException.class);
    }

    private Path reportDirectory() throws IOException {
        Path reports = tempDir.resolve("reports-" + System.nanoTime());
        Files.createDirectories(reports);
        return reports;
    }

    /** Writes the two-suite report shape production always produces. */
    private static void writeFullReport(Path reports, String exampleCases) throws IOException {
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, exampleCases);
        writeSuite(reports, "TEST-Correctness.xml", CORRECTNESS,
                passingCases(PAIR_SUM.fullCount() - PAIR_SUM.exampleCount()));
    }

    private static String passingCases(int count) {
        StringBuilder cases = new StringBuilder();
        for (int i = 0; i < count; i++) {
            cases.append(caseXml("passingCase" + i, ""));
        }
        return cases.toString();
    }

    private static String caseXml(String name, String result) {
        return "<testcase name=\"" + name + "\">" + result + "</testcase>";
    }

    private static void writeSuite(Path directory, String fileName, String className, String cases)
            throws IOException {
        writeRaw(directory, fileName,
                "<testsuite name=\"" + className + "\">" + cases + "</testsuite>");
    }

    private static void writeRaw(Path directory, String fileName, String xml) throws IOException {
        Files.writeString(directory.resolve(fileName), xml, StandardCharsets.UTF_8);
    }
}
