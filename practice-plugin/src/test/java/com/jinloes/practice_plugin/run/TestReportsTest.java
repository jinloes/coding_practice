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
    void acceptsAnExamplesReportUsingTheCatalogCount() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 0);

        assertThat(result.status()).isEqualTo(PASSED);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("All " + PAIR_SUM.exampleCount() + " supplied cases passed");
    }

    @Test
    void acceptsAFullReportUsingTheCatalogCountAndExpectedClasses() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));
        writeSuite(reports, "TEST-Correctness.xml", CORRECTNESS,
                passingCases(PAIR_SUM.fullCount() - PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.fullCount(), true, 0);

        assertThat(result.status()).isEqualTo(PASSED);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.fullCount());
        assertThat(result.failures()).isZero();
    }

    @Test
    void classifiesAssertionFailuresAndPreservesDetailedMessages() throws Exception {
        Path reports = reportDirectory();
        String failure = caseXml(
                "findsTypicalPair",
                "<failure type=\"org.opentest4j.AssertionFailedError\" message=\"expected pair\">"
                        + "at com.jinloes.practice.ExamplesTest.findsTypicalPair(ExamplesTest.java:12)"
                        + "</failure>");
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES,
                failure + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 0);

        assertThat(result.status()).isEqualTo(ASSERTION_FAILED);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isEqualTo(1);
        assertThat(result.details())
                .contains(EXAMPLES + ".findsTypicalPair", "expected pair", "ExamplesTest.java:12");
    }

    @Test
    void classifiesRuntimeErrorsAndPreservesDetailedMessages() throws Exception {
        Path reports = reportDirectory();
        String error = caseXml(
                "failsAtRuntime",
                "<error type=\"java.lang.IllegalStateException\" message=\"broken setup\">"
                        + "java.lang.IllegalStateException: broken setup"
                        + "</error>");
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES,
                error + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 0);

        assertThat(result.status()).isEqualTo(RUNTIME_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isEqualTo(1);
        assertThat(result.details()).contains(EXAMPLES + ".failsAtRuntime", "broken setup");
    }

    @Test
    void classifiesTimeoutFailures() throws Exception {
        Path reports = reportDirectory();
        String timeout = caseXml(
                "timesOut",
                "<error type=\"java.util.concurrent.TimeoutException\" message=\"5 seconds\">"
                        + "test exceeded its deadline"
                        + "</error>");
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES,
                timeout + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 0);

        assertThat(result.status()).isEqualTo(TIMED_OUT);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isEqualTo(1);
    }

    @Test
    void rejectsAReportContainingSkippedTests() throws Exception {
        Path reports = reportDirectory();
        String skipped = caseXml("skippedCase", "<skipped/>");
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES,
                skipped + passingCases(PAIR_SUM.exampleCount() - 1));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("Incomplete test run", "skipped 1");
    }

    @Test
    void rejectsAReportMissingFromTheExpectedDirectory() throws Exception {
        CheckResult result = TestReports.read(
                tempDir.resolve("missing-reports"),
                PAIR_SUM.exampleCount(),
                false,
                0);

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

        assertThatThrownBy(() -> TestReports.read(reports, 2, false, 0))
                .isInstanceOf(IOException.class);
    }

    @Test
    void rejectsReportsWithTheWrongTestCount() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount() + 1, false, 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.failures()).isZero();
        assertThat(result.details()).contains("Incomplete test run");
    }

    @Test
    void rejectsAFullReportMissingAnExpectedSuiteClass() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), true, 0);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
        assertThat(result.details()).contains(CORRECTNESS);
    }

    @Test
    void rejectsAValidReportWhenGradleExitsNonzero() throws Exception {
        Path reports = reportDirectory();
        writeSuite(reports, "TEST-Examples.xml", EXAMPLES, passingCases(PAIR_SUM.exampleCount()));

        CheckResult result = TestReports.read(reports, PAIR_SUM.exampleCount(), false, 7);

        assertThat(result.status()).isEqualTo(RUNNER_ERROR);
        assertThat(result.tests()).isEqualTo(PAIR_SUM.exampleCount());
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
        assertThatThrownBy(() -> TestReports.read(reports, 1, false, 0)).isInstanceOf(IOException.class);
    }

    private Path reportDirectory() throws IOException {
        Path reports = tempDir.resolve("reports-" + System.nanoTime());
        Files.createDirectories(reports);
        return reports;
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
