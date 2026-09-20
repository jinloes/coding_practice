package com.jinloes.practice_plugin.run;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Reads the probe output written by the isolated verification JVM and turns doubling-size
 * measurements into the coarsest growth class that wall-clock timing can honestly support.
 */
public final class ComplexityAnalysis {
    static final String FILE = "complexity.txt";
    private static final String SCHEMA = "1";
    private static final long MAX_FILE_BYTES = 64_000;
    private static final int MIN_SAMPLES = 3;
    /**
     * Below this <em>total</em> measured wall-clock time for one call, timer resolution and
     * scheduling jitter dominate the reading. A tiny per-unit value is trustworthy once the raw
     * call it was divided down from cleared this bar; dividing a reliably measured call by a large
     * unit count can legitimately produce a small per-unit number, and that is not the same failure
     * as the call itself being too brief to time.
     */
    private static final long MIN_TRUSTED_RAW_NANOS = 50_000;
    /** Below this, allocation is bookkeeping noise rather than a data structure. */
    private static final long CONSTANT_SPACE_BYTES = 8_192;

    private ComplexityAnalysis() {}

    public static Optional<ComplexityReport> read(Path resultDirectory) throws IOException {
        Path file = resultDirectory.resolve(FILE);
        if (!Files.isRegularFile(file)) {
            return Optional.empty();
        }
        if (Files.size(file) > MAX_FILE_BYTES) {
            throw new IOException("Complexity report exceeds its size limit.");
        }
        return parse(Files.readString(file, StandardCharsets.UTF_8));
    }

    static Optional<ComplexityReport> parse(String text) throws IOException {
        List<ComplexityReport.Sample> samples = new ArrayList<>();
        String unit = "call";
        String status = "";
        String message = "";
        boolean schemaSeen = false;
        for (String line : text.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.startsWith("schema=")) {
                schemaSeen = SCHEMA.equals(value(trimmed, "schema"));
            } else if (trimmed.startsWith("unit=")) {
                unit = value(trimmed, "unit");
            } else if (trimmed.startsWith("status=")) {
                status = value(trimmed, "status");
            } else if (trimmed.startsWith("message=")) {
                message = value(trimmed, "message");
            } else if (trimmed.startsWith("size=")) {
                samples.add(sample(trimmed));
            }
        }
        if (!schemaSeen) {
            throw new IOException("Unsupported complexity report.");
        }
        if (!"ok".equals(status)) {
            return Optional.of(new ComplexityReport(unit, ComplexityReport.INCONCLUSIVE,
                    ComplexityReport.INCONCLUSIVE, samples,
                    message.isBlank() ? "The probe did not finish." : "The probe stopped: " + message));
        }
        return Optional.of(classify(unit, samples, message));
    }

    static ComplexityReport classify(String unit, List<ComplexityReport.Sample> samples, String message) {
        List<Long> times = samples.stream().map(ComplexityReport.Sample::nanos).toList();
        List<Long> rawTimes = samples.stream().map(ComplexityReport.Sample::rawNanos).toList();
        List<Long> allocations = samples.stream().map(ComplexityReport.Sample::bytes).toList();
        boolean tooBriefToTrust = samples.size() < MIN_SAMPLES || max(rawTimes) < MIN_TRUSTED_RAW_NANOS;
        String timeClass = tooBriefToTrust
                ? ComplexityReport.INCONCLUSIVE
                : growth(doublingRatios(samples, times));
        String spaceClass;
        if (allocations.stream().anyMatch(value -> value < 0)) {
            spaceClass = ComplexityReport.INCONCLUSIVE;
        } else if (max(allocations) < CONSTANT_SPACE_BYTES) {
            spaceClass = "O(1)";
        } else if (samples.size() < MIN_SAMPLES) {
            spaceClass = ComplexityReport.INCONCLUSIVE;
        } else {
            spaceClass = growth(doublingRatios(samples, allocations));
        }
        String note = message;
        if (ComplexityReport.INCONCLUSIVE.equals(timeClass) && samples.size() >= MIN_SAMPLES
                && max(rawTimes) < MIN_TRUSTED_RAW_NANOS) {
            note = join(note, "Each measured call was too brief to time reliably.");
        }
        return new ComplexityReport(unit, timeClass, spaceClass, samples, note);
    }

    /**
     * Ratios between successive measurements whose sizes actually doubled, so a workload that
     * skipped or reordered sizes cannot masquerade as a growth rate.
     */
    private static List<Double> doublingRatios(List<ComplexityReport.Sample> samples, List<Long> values) {
        List<Double> ratios = new ArrayList<>();
        for (int i = 1; i < samples.size(); i++) {
            long previous = values.get(i - 1);
            if (previous <= 0 || samples.get(i).size() != samples.get(i - 1).size() * 2) {
                continue;
            }
            ratios.add((double) values.get(i) / previous);
        }
        return ratios.size() > 3 ? ratios.subList(ratios.size() - 3, ratios.size()) : ratios;
    }

    private static String growth(List<Double> ratios) {
        if (ratios.size() < MIN_SAMPLES - 1) {
            return ComplexityReport.INCONCLUSIVE;
        }
        List<Double> sorted = ratios.stream().sorted().toList();
        double spread = sorted.get(sorted.size() - 1) / Math.max(sorted.get(0), 0.0001);
        if (spread > 4.0) {
            return ComplexityReport.INCONCLUSIVE;
        }
        double median = sorted.get(sorted.size() / 2);
        if (median < 1.4) {
            return "O(1) or O(log n)";
        }
        if (median < 2.6) {
            return "O(n) or O(n log n)";
        }
        if (median < 6.0) {
            return "O(n^2)";
        }
        return "worse than O(n^2)";
    }

    private static long max(List<Long> values) {
        return values.stream().mapToLong(Long::longValue).max().orElse(0);
    }

    private static String join(String first, String second) {
        return first.isBlank() ? second : first + " " + second;
    }

    private static String value(String line, String key) {
        return line.substring(key.length() + 1).trim();
    }

    private static ComplexityReport.Sample sample(String line) throws IOException {
        int size = 0;
        long nanos = -1;
        long bytes = -1;
        long rawNanos = -1;
        for (String field : line.split("\\s+")) {
            int split = field.indexOf('=');
            if (split < 0) {
                continue;
            }
            String key = field.substring(0, split);
            String raw = field.substring(split + 1);
            try {
                switch (key) {
                    case "size" -> size = Integer.parseInt(raw);
                    case "nanos" -> nanos = Long.parseLong(raw);
                    case "bytes" -> bytes = Long.parseLong(raw);
                    case "rawNanos" -> rawNanos = Long.parseLong(raw);
                    default -> { }
                }
            } catch (NumberFormatException exception) {
                throw new IOException("Invalid complexity measurement: " + line, exception);
            }
        }
        if (size <= 0 || nanos < 0 || rawNanos < 0) {
            throw new IOException("Incomplete complexity measurement: " + line);
        }
        return new ComplexityReport.Sample(size, nanos, bytes, rawNanos);
    }
}
