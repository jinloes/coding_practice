package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: s-e:p, ... (comma-separated start-end:profit jobs), for example: 1-3:50, 2-4:10, 3-5:40, 3-6:70";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.maxProfit(
                new int[]{1, 2, 3, 3}, new int[]{3, 4, 5, 6}, new int[]{50, 10, 40, 70}))
                .as("Example 1").isEqualTo(120);
        assertThat(solution.maxProfit(
                new int[]{1, 2, 3, 4, 6}, new int[]{3, 5, 10, 6, 9}, new int[]{20, 20, 100, 70, 60}))
                .as("Example 2").isEqualTo(150);
        assertThat(solution.maxProfit(
                new int[]{1, 1, 1}, new int[]{2, 3, 4}, new int[]{5, 6, 4}))
                .as("Example 3").isEqualTo(6);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("Provide at least one job. " + USAGE);
            return;
        }
        String[] parts = input.split(",");
        int[] starts = new int[parts.length];
        int[] ends = new int[parts.length];
        int[] profits = new int[parts.length];
        try {
            for (int i = 0; i < parts.length; i++) {
                String token = parts[i].trim();
                int colon = token.indexOf(':');
                int dash = token.indexOf('-');
                if (dash <= 0 || colon <= dash + 1 || colon == token.length() - 1) {
                    System.out.println("Invalid job '" + token + "'. " + USAGE);
                    return;
                }
                starts[i] = Integer.parseInt(token.substring(0, dash).trim());
                ends[i] = Integer.parseInt(token.substring(dash + 1, colon).trim());
                profits[i] = Integer.parseInt(token.substring(colon + 1).trim());
            }
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer in job. " + USAGE);
            return;
        }
        System.out.println(new Solution().maxProfit(starts, ends, profits));
    }
}
