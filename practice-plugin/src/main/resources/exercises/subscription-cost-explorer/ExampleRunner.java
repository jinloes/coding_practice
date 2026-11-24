package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: year; product price start end|-, ..., for example: 2025; Jira 1000 2025-03-15 -, Confluence 500 2025-02-20 2025-04-01";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.monthlyCostReport(
                List.of(new Solution.Subscription("Jira", 1000L, "2025-03-15", null)), 2025))
                .as("Example 1")
                .containsExactly(0, 0, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000, 1000);

        assertThat(solution.monthlyCostReport(
                List.of(
                        new Solution.Subscription("Jira", 1000L, "2024-11-01", "2025-02-10"),
                        new Solution.Subscription("Confluence", 500L, "2025-02-20", "2025-04-01")),
                2025))
                .as("Example 2")
                .containsExactly(1000, 1500, 500, 500, 0, 0, 0, 0, 0, 0, 0, 0);

        assertThat(solution.monthlyCostReport(
                List.of(new Solution.Subscription("Jira", 1000L, "2026-01-01", null)), 2025))
                .as("Example 3")
                .containsExactly(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'year; subscriptions'. " + USAGE);
            return;
        }
        try {
            int year = Integer.parseInt(sections[0].trim());
            List<Solution.Subscription> subs = new ArrayList<>();
            if (!sections[1].trim().isEmpty()) {
                for (String raw : sections[1].split(",")) {
                    String[] parts = raw.trim().split("\\s+");
                    if (parts.length != 4) {
                        System.out.println("Invalid subscription '" + raw.trim() + "'. " + USAGE);
                        return;
                    }
                    String endDate = "-".equals(parts[3]) ? null : parts[3];
                    subs.add(new Solution.Subscription(
                            parts[0], Long.parseLong(parts[1]), parts[2], endDate));
                }
            }
            System.out.println(Arrays.toString(new Solution().monthlyCostReport(subs, year)));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid number in input. " + USAGE);
        }
    }
}
