package com.jinloes.practice_plugin.run;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComplexityAnalysisTest {
    @TempDir
    Path tempDir;

    @Test
    void readsDoublingMeasurementsAndNamesTheGrowthClass() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=100000 bytes=40000 rawNanos=100000
                size=2000 nanos=200000 bytes=80000 rawNanos=200000
                size=4000 nanos=400000 bytes=160000 rawNanos=400000
                size=8000 nanos=800000 bytes=320000 rawNanos=800000
                message=
                status=ok
                """);

        assertThat(report.timeClass()).as("doubling time for doubling input is linear growth")
                .isEqualTo("O(n) or O(n log n)");
        assertThat(report.spaceClass()).as("doubling allocation for doubling input is linear growth")
                .isEqualTo("O(n) or O(n log n)");
        assertThat(report.samples()).extracting(ComplexityReport.Sample::size)
                .containsExactly(1000, 2000, 4000, 8000);
    }

    @Test
    void quadrupledTimeIsReportedAsQuadratic() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=0 rawNanos=1000000
                size=2000 nanos=4000000 bytes=0 rawNanos=4000000
                size=4000 nanos=16000000 bytes=0 rawNanos=16000000
                size=8000 nanos=64000000 bytes=0 rawNanos=64000000
                status=ok
                """);

        assertThat(report.timeClass()).as("quadrupling per doubling is quadratic").isEqualTo("O(n^2)");
        assertThat(report.spaceClass()).as("a solution that allocates nothing uses constant space")
                .isEqualTo("O(1)");
    }

    @Test
    void veryFastGrowthIsReportedAsWorseThanQuadratic() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=10000 rawNanos=1000000
                size=2000 nanos=7000000 bytes=70000 rawNanos=7000000
                size=4000 nanos=49000000 bytes=490000 rawNanos=49000000
                status=ok
                """);

        assertThat(report.timeClass()).isEqualTo("worse than O(n^2)");
        assertThat(report.spaceClass()).isEqualTo("worse than O(n^2)");
    }

    @Test
    void flatTimeIsReportedAsTheSublinearRangeTimingCannotSplit() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=search
                size=4096 nanos=20000 bytes=0 rawNanos=200000
                size=8192 nanos=21000 bytes=0 rawNanos=210000
                size=16384 nanos=22000 bytes=0 rawNanos=220000
                size=32768 nanos=23000 bytes=0 rawNanos=230000
                status=ok
                """);

        assertThat(report.timeClass())
                .as("wall-clock timing cannot separate constant from logarithmic growth")
                .isEqualTo("O(1) or O(log n)");
    }

    @Test
    void measurementsTooFastToTimeAreInconclusiveRatherThanGuessed() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=operation
                size=1000 nanos=30 bytes=0 rawNanos=30
                size=2000 nanos=31 bytes=0 rawNanos=31
                size=4000 nanos=90 bytes=0 rawNanos=90
                message=probe ran
                status=ok
                """);

        assertThat(report.timeClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.note()).contains("probe ran", "too brief to time reliably");
    }

    @Test
    void aTinyPerUnitValueFromALargeUnitCountIsNotTreatedAsUnreliable() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=operation
                size=4096 nanos=2 bytes=0 rawNanos=100000
                size=8192 nanos=2 bytes=0 rawNanos=200000
                size=16384 nanos=2 bytes=0 rawNanos=400000
                size=32768 nanos=2 bytes=0 rawNanos=800000
                status=ok
                """);

        assertThat(report.timeClass())
                .as("a call that ran long enough to trust must not be inconclusive just because "
                        + "dividing by a large unit count made the per-unit value tiny")
                .isEqualTo("O(1) or O(log n)");
    }

    @Test
    void noisyMeasurementsAreInconclusiveRatherThanGuessed() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=0 rawNanos=1000000
                size=2000 nanos=20000000 bytes=0 rawNanos=20000000
                size=4000 nanos=22000000 bytes=0 rawNanos=22000000
                size=8000 nanos=25000000 bytes=0 rawNanos=25000000
                status=ok
                """);

        assertThat(report.timeClass())
                .as("ratios spread from 1.1x to 20x describe noise, not a growth rate")
                .isEqualTo(ComplexityReport.INCONCLUSIVE);
    }

    @Test
    void sizesThatDidNotDoubleAreNotTreatedAsAGrowthRate() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=0 rawNanos=1000000
                size=1500 nanos=2000000 bytes=0 rawNanos=2000000
                size=9000 nanos=4000000 bytes=0 rawNanos=4000000
                status=ok
                """);

        assertThat(report.timeClass())
                .as("only measurements whose size actually doubled can imply a growth rate")
                .isEqualTo(ComplexityReport.INCONCLUSIVE);
    }

    @Test
    void afailedProbeIsReportedWithoutAGrowthClass() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                ignored=true
                size=1000 nanos=1000000 bytes=0 rawNanos=1000000
                message=OutOfMemoryError
                status=failed
                """);

        assertThat(report.timeClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.spaceClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.note()).contains("OutOfMemoryError");

        ComplexityReport defaultFailure = parse("""

                   schema=1
                   size=1000 nanos=1000000 bytes=0 rawNanos=1000000 ignored-field other=1
                   status=failed
                """);
        assertThat(defaultFailure.unit()).isEqualTo("call");
        assertThat(defaultFailure.note()).isEqualTo("The probe did not finish.");
    }

    @Test
    void unmeasuredAllocationIsInconclusiveRatherThanConstant() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=-1 rawNanos=1000000
                size=2000 nanos=2000000 bytes=-1 rawNanos=2000000
                size=4000 nanos=4000000 bytes=-1 rawNanos=4000000
                status=ok
                """);

        assertThat(report.spaceClass())
                .as("a JVM without allocation counters must not look like constant space")
                .isEqualTo(ComplexityReport.INCONCLUSIVE);
    }

    @Test
    void tooFewSamplesAreInconclusiveForTimeAndSpace() {
        ComplexityReport report = ComplexityAnalysis.classify("call",
                List.of(
                        new ComplexityReport.Sample(1000, 100_000, 10_000, 100_000),
                        new ComplexityReport.Sample(2000, 200_000, 20_000, 200_000)),
                "");

        assertThat(report.timeClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.spaceClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
    }

    @Test
    void tooBriefMeasurementsWithoutProbeMessageUseOnlyTheReliabilityNote() {
        ComplexityReport report = ComplexityAnalysis.classify("call",
                List.of(
                        new ComplexityReport.Sample(1000, 10, 0, 10),
                        new ComplexityReport.Sample(2000, 20, 0, 20),
                        new ComplexityReport.Sample(4000, 30, 0, 30)),
                "");

        assertThat(report.note()).isEqualTo("Each measured call was too brief to time reliably.");
    }

    @Test
    void zeroMeasurementsAndOldRatiosDoNotDriveTheGrowthClass() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=0 bytes=10000 rawNanos=100000
                size=2000 nanos=100000 bytes=20000 rawNanos=100000
                size=4000 nanos=200000 bytes=40000 rawNanos=200000
                size=8000 nanos=400000 bytes=80000 rawNanos=400000
                size=16000 nanos=800000 bytes=160000 rawNanos=800000
                size=32000 nanos=1600000 bytes=320000 rawNanos=1600000
                status=ok
                message=stable enough
                """);

        assertThat(report.timeClass())
                .as("the initial zero ratio is skipped, and only the last three ratios are classified")
                .isEqualTo("O(n) or O(n log n)");
        assertThat(report.note()).isEqualTo("stable enough");
    }

    @Test
    void unreadableReportsAreRejected() {
        assertThatThrownBy(() -> ComplexityAnalysis.parse("size=10 nanos=5 bytes=0\nstatus=ok\n"))
                .as("a report without a supported schema must not be interpreted")
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=2\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=1\nsize=10 nanos=x\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=1\nsize=0 nanos=5 rawNanos=5\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=1\nsize=10 rawNanos=5\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void renderingComparesTheMeasurementWithTheIntendedComplexity() {
        ComplexityReport report = new ComplexityReport("call", "O(n) or O(n log n)", "O(1)",
                List.of(new ComplexityReport.Sample(1000, 250_000, 512, 250_000)), "");

        String linear = report.render("O(n)", "O(1)");
        assertThat(linear).as("a matching measurement should say so")
                .contains("consistent with the intended O(n)")
                .contains("consistent with the intended O(1)")
                .contains("not proof of complexity");

        assertThat(report.render("O(log n)", "O(1)"))
                .as("a measurement slower than intended must not claim consistency")
                .contains("Time:  O(n) or O(n log n) (intended O(log n))");
    }

    @Test
    void renderingHandlesBlankIntentionsInconclusiveMeasurementsAndDisplayUnits() {
        ComplexityReport report = new ComplexityReport("batch", ComplexityReport.INCONCLUSIVE,
                ComplexityReport.INCONCLUSIVE,
                List.of(
                        new ComplexityReport.Sample(1, -1, -1, 1),
                        new ComplexityReport.Sample(2, 9_999, 10_239, 1),
                        new ComplexityReport.Sample(4, 10_000, 10_240, 1),
                        new ComplexityReport.Sample(8, 10_000_000, 10_485_760, 1)),
                "probe note");

        assertThat(report.render(null, " "))
                .contains("         1               -               -")
                .contains("         2         9999 ns         10239 B")
                .contains("         4           10 us          10 KiB")
                .contains("         8           10 ms          10 MiB")
                .contains("probe note")
                .contains("Time:  inconclusive")
                .doesNotContain("intended  ");
        assertThat(report.render("O(n)", "O(n)"))
                .contains("measurement was inconclusive");
    }

    @Test
    void reportObjectsDefensivelyRejectInvalidConstruction() {
        assertThatThrownBy(() -> new ComplexityReport.Sample(0, 1, 1, 1))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ComplexityReport(null, "time", "space", List.of(), ""))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ComplexityReport("unit", null, "space", List.of(), ""))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ComplexityReport("unit", "time", null, List.of(), ""))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ComplexityReport("unit", "time", "space", null, ""))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new ComplexityReport("unit", "time", "space", List.of(), null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void readReturnsEmptyForMissingReportAndRejectsOversizedFiles() throws Exception {
        assertThat(ComplexityAnalysis.read(tempDir)).isEmpty();

        Files.writeString(tempDir.resolve(ComplexityAnalysis.FILE), "x".repeat(64_001),
                StandardCharsets.UTF_8);

        assertThatThrownBy(() -> ComplexityAnalysis.read(tempDir))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("size limit");
    }

    private static ComplexityReport parse(String text) throws IOException {
        return ComplexityAnalysis.parse(text).orElseThrow();
    }
}
