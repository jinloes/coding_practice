package com.jinloes.practice;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Measures how one solution scales. Runs only after the correctness suite passes, inside the
 * isolated verification JVM, and never decides whether an attempt passed.
 */
public final class ComplexityProbe {
    private static final int WARMUP_REPETITIONS = 40;
    private static final int MEASURED_REPETITIONS = 7;
    private static final long BUDGET_NANOS = 6_000_000_000L;

    public static void main(String[] args) {
        Path directory = Path.of(args.length > 0 ? args[0] : ".practice-results");
        StringBuilder measurements = new StringBuilder();
        String status = "ok";
        String message = "";
        try {
            long deadline = System.nanoTime() + BUDGET_NANOS;
            warmUp();
            int completed = 0;
            for (int size : Workload.SIZES) {
                if (System.nanoTime() > deadline) {
                    message = "Stopped after " + completed + " of " + Workload.SIZES.length
                            + " sizes to stay within its time budget.";
                    break;
                }
                Measurement measurement = measure(size);
                measurements.append("size=").append(size)
                        .append(" nanos=").append(measurement.nanos())
                        .append(" bytes=").append(measurement.bytes())
                        .append(" rawNanos=").append(measurement.rawNanos())
                        .append("\n");
                completed++;
            }
        } catch (RuntimeException | StackOverflowError | OutOfMemoryError failure) {
            status = "failed";
            message = failure.getClass().getSimpleName()
                    + (failure.getMessage() == null ? "" : ": " + failure.getMessage());
        }
        String report = "schema=1\nunit=" + Workload.UNIT + "\n" + measurements
                + "message=" + message.replace('\n', ' ') + "\nstatus=" + status + "\n";
        try {
            Files.createDirectories(directory);
            Files.writeString(directory.resolve("complexity.txt"), report, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            System.out.println("Could not write the complexity report: " + exception.getMessage());
        }
        System.out.println(report);
    }

    private static void warmUp() {
        int size = Arrays.stream(Workload.SIZES).min().orElse(1);
        for (int i = 0; i < WARMUP_REPETITIONS; i++) {
            Workload.run(Workload.prepare(size));
        }
    }

    private static Measurement measure(int size) {
        long units = Math.max(1, Workload.units(size));
        List<Long> times = new ArrayList<>();
        List<Long> rawTimes = new ArrayList<>();
        List<Long> allocations = new ArrayList<>();
        for (int repetition = 0; repetition < MEASURED_REPETITIONS; repetition++) {
            Object state = Workload.prepare(size);
            long allocatedBefore = allocatedBytes();
            long started = System.nanoTime();
            Workload.run(state);
            long elapsed = System.nanoTime() - started;
            long allocatedAfter = allocatedBytes();
            times.add(elapsed / units);
            rawTimes.add(elapsed);
            allocations.add(allocatedBefore < 0 || allocatedAfter < 0
                    ? -1 : Math.max(0, allocatedAfter - allocatedBefore) / units);
        }
        return new Measurement(median(times), median(allocations), median(rawTimes));
    }

    private static long median(List<Long> values) {
        List<Long> sorted = values.stream().sorted().toList();
        return sorted.get(sorted.size() / 2);
    }

    /**
     * Total bytes this thread has allocated, which measures auxiliary allocation rather than peak
     * live memory. Peak live size depends on when the collector happens to run, so it is not
     * reported.
     */
    private static long allocatedBytes() {
        var threads = ManagementFactory.getThreadMXBean();
        if (threads instanceof com.sun.management.ThreadMXBean measured
                && measured.isThreadAllocatedMemoryEnabled()) {
            return measured.getCurrentThreadAllocatedBytes();
        }
        return -1;
    }

    private record Measurement(long nanos, long bytes, long rawNanos) {
    }
}
