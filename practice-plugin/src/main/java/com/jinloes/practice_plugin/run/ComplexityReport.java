package com.jinloes.practice_plugin.run;

import java.util.List;
import java.util.Objects;

/**
 * A measured scaling profile for one solution. The measurement is empirical, so the classes it
 * reports are ranges that timing can actually distinguish, never a proof of complexity.
 */
public record ComplexityReport(
        String unit,
        String timeClass,
        String spaceClass,
        List<Sample> samples,
        String note
) {
    public static final String INCONCLUSIVE = "inconclusive";

    public record Sample(int size, long nanos, long bytes) {
        public Sample {
            if (size <= 0) {
                throw new IllegalArgumentException("Sample size must be positive: " + size);
            }
        }
    }

    public ComplexityReport {
        unit = Objects.requireNonNull(unit, "unit");
        timeClass = Objects.requireNonNull(timeClass, "timeClass");
        spaceClass = Objects.requireNonNull(spaceClass, "spaceClass");
        samples = List.copyOf(Objects.requireNonNull(samples, "samples"));
        note = Objects.requireNonNull(note, "note");
    }

    public String render(String intendedTime, String intendedSpace) {
        StringBuilder text = new StringBuilder("Measured scaling (per ").append(unit).append(")\n");
        text.append(String.format("%10s  %14s  %14s%n", "n", "time", "allocated"));
        for (Sample sample : samples) {
            text.append(String.format("%10d  %14s  %14s%n",
                    sample.size(), time(sample.nanos()), bytes(sample.bytes())));
        }
        text.append("\nTime:  ").append(timeClass).append(compare(timeClass, intendedTime))
                .append("\nSpace: ").append(spaceClass).append(compare(spaceClass, intendedSpace));
        if (!note.isBlank()) {
            text.append("\n").append(note);
        }
        text.append("\nMeasured on this machine under load; it indicates growth, not proof of complexity.");
        return text.toString();
    }

    private static String compare(String measured, String intended) {
        if (intended == null || intended.isBlank()) {
            return "";
        }
        if (INCONCLUSIVE.equals(measured)) {
            return " (intended " + intended + "; measurement was inconclusive)";
        }
        return measured.contains(intended)
                ? " (consistent with the intended " + intended + ")"
                : " (intended " + intended + ")";
    }

    private static String time(long nanos) {
        if (nanos < 0) {
            return "-";
        }
        if (nanos < 10_000) {
            return nanos + " ns";
        }
        if (nanos < 10_000_000) {
            return nanos / 1_000 + " us";
        }
        return nanos / 1_000_000 + " ms";
    }

    private static String bytes(long value) {
        if (value < 0) {
            return "-";
        }
        if (value < 10_240) {
            return value + " B";
        }
        if (value < 10_485_760) {
            return value / 1024 + " KiB";
        }
        return value / 1_048_576 + " MiB";
    }
}
