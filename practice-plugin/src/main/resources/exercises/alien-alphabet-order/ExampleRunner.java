package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: words in sorted order separated by commas, using lowercase letters only, for example: zy,zx,yx";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.alienOrder(new String[]{"zy", "zx", "yx"})).as("Example 1").isEqualTo("zyx");
        assertThat(Solution.alienOrder(new String[]{"abc", "ab"})).as("Example 2").isEmpty();
        assertThat(Solution.alienOrder(new String[]{"dog"})).as("Example 3").hasSize(3).containsOnlyOnce("d").containsOnlyOnce("o").containsOnlyOnce("g");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] words = input.split(",", -1);
        for (String word : words) {
            if (!word.matches("[a-z]+")) {
                System.out.println("Could not read \"" + input + "\" as lowercase words. " + USAGE);
                return;
            }
        }
        String order = Solution.alienOrder(words);
        System.out.println("alienOrder(" + Arrays.toString(words) + ") = "
                + (order.isEmpty() ? "\"\" (inconsistent)" : "\"" + order + "\""));
    }
}
