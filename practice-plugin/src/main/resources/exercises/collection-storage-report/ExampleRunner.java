package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: semicolon-separated put <file> <bytes> <c1,c2 or -> and top <limit>, for example: put report.pdf 200 finance,audit; put chart.png 150 finance; put raw.csv 100 -; top 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution();
        first.putFile("report.pdf", 200, List.of("finance", "audit"));
        first.putFile("chart.png", 150, List.of("finance"));
        first.putFile("raw.csv", 100, List.of());
        assertThat(first.totalBytes()).as("Example 1 total").isEqualTo(450L);
        assertThat(first.topCollections(2)).as("Example 1 top").containsExactly("finance", "audit");

        Solution second = new Solution();
        second.putFile("a.log", 300, List.of("ops", "ops"));
        second.putFile("b.log", 300, List.of("eng"));
        assertThat(second.totalBytes()).as("Example 2 total").isEqualTo(600L);
        assertThat(second.topCollections(2)).as("Example 2 top").containsExactly("eng", "ops");

        Solution third = new Solution();
        third.putFile("build.zip", 500, List.of("legacy"));
        third.putFile("build.zip", 120, List.of("release"));
        assertThat(third.totalBytes()).as("Example 3 total").isEqualTo(120L);
        assertThat(third.topCollections(2)).as("Example 3 top").containsExactly("release");

        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            Solution report = new Solution();
            List<String> lastTop = null;
            for (String clause : input.split(";")) {
                String[] parts = clause.trim().split("\\s+");
                if (parts.length == 4 && "put".equals(parts[0])) {
                    List<String> tags = "-".equals(parts[3])
                            ? List.of()
                            : Arrays.asList(parts[3].split(","));
                    report.putFile(parts[1], Long.parseLong(parts[2]), tags);
                } else if (parts.length == 2 && "top".equals(parts[0])) {
                    lastTop = report.topCollections(Integer.parseInt(parts[1]));
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (lastTop == null) {
                throw new IllegalArgumentException();
            }
            System.out.println("totalBytes=" + report.totalBytes() + ", top=" + lastTop);
        } catch (RuntimeException exception) {
            System.out.println("Could not read operations. " + USAGE);
        }
    }
}
