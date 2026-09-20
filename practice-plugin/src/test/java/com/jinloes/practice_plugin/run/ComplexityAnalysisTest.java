package com.jinloes.practice_plugin.run;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ComplexityAnalysisTest {
    @Test
    void readsDoublingMeasurementsAndNamesTheGrowthClass() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=100000 bytes=40000
                size=2000 nanos=200000 bytes=80000
                size=4000 nanos=400000 bytes=160000
                size=8000 nanos=800000 bytes=320000
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
                size=1000 nanos=1000000 bytes=0
                size=2000 nanos=4000000 bytes=0
                size=4000 nanos=16000000 bytes=0
                size=8000 nanos=64000000 bytes=0
                status=ok
                """);

        assertThat(report.timeClass()).as("quadrupling per doubling is quadratic").isEqualTo("O(n^2)");
        assertThat(report.spaceClass()).as("a solution that allocates nothing uses constant space")
                .isEqualTo("O(1)");
    }

    @Test
    void flatTimeIsReportedAsTheSublinearRangeTimingCannotSplit() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=search
                size=4096 nanos=20000 bytes=0
                size=8192 nanos=21000 bytes=0
                size=16384 nanos=22000 bytes=0
                size=32768 nanos=23000 bytes=0
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
                size=1000 nanos=30 bytes=0
                size=2000 nanos=31 bytes=0
                size=4000 nanos=90 bytes=0
                status=ok
                """);

        assertThat(report.timeClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.note()).contains("too fast to time reliably");
    }

    @Test
    void noisyMeasurementsAreInconclusiveRatherThanGuessed() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=0
                size=2000 nanos=20000000 bytes=0
                size=4000 nanos=22000000 bytes=0
                size=8000 nanos=25000000 bytes=0
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
                size=1000 nanos=1000000 bytes=0
                size=1500 nanos=2000000 bytes=0
                size=9000 nanos=4000000 bytes=0
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
                size=1000 nanos=1000000 bytes=0
                message=OutOfMemoryError
                status=failed
                """);

        assertThat(report.timeClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.spaceClass()).isEqualTo(ComplexityReport.INCONCLUSIVE);
        assertThat(report.note()).contains("OutOfMemoryError");
    }

    @Test
    void unmeasuredAllocationIsInconclusiveRatherThanConstant() throws Exception {
        ComplexityReport report = parse("""
                schema=1
                unit=call
                size=1000 nanos=1000000 bytes=-1
                size=2000 nanos=2000000 bytes=-1
                size=4000 nanos=4000000 bytes=-1
                status=ok
                """);

        assertThat(report.spaceClass())
                .as("a JVM without allocation counters must not look like constant space")
                .isEqualTo(ComplexityReport.INCONCLUSIVE);
    }

    @Test
    void unreadableReportsAreRejected() {
        assertThatThrownBy(() -> ComplexityAnalysis.parse("size=10 nanos=5 bytes=0\nstatus=ok\n"))
                .as("a report without a supported schema must not be interpreted")
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=1\nsize=10 nanos=x\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
        assertThatThrownBy(() -> ComplexityAnalysis.parse("schema=1\nsize=0 nanos=5\nstatus=ok\n"))
                .isInstanceOf(IOException.class);
    }

    @Test
    void renderingComparesTheMeasurementWithTheIntendedComplexity() {
        ComplexityReport report = new ComplexityReport("call", "O(n) or O(n log n)", "O(1)",
                List.of(new ComplexityReport.Sample(1000, 250_000, 512)), "");

        String linear = report.render("O(n)", "O(1)");
        assertThat(linear).as("a matching measurement should say so")
                .contains("consistent with the intended O(n)")
                .contains("consistent with the intended O(1)")
                .contains("not proof of complexity");

        assertThat(report.render("O(log n)", "O(1)"))
                .as("a measurement slower than intended must not claim consistency")
                .contains("Time:  O(n) or O(n log n) (intended O(log n))");
    }

    private static ComplexityReport parse(String text) throws IOException {
        return ComplexityAnalysis.parse(text).orElseThrow();
    }
}
