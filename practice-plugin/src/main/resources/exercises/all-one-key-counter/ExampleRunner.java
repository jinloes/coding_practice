package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: inc apple, inc banana, inc apple, max, min";

    private ExampleRunner() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution();
        first.inc("apple");
        first.inc("banana");
        first.inc("apple");
        assertThat(first.getMaxKey()).isEqualTo("apple");
        assertThat(first.getMinKey()).isEqualTo("banana");
        Solution second = new Solution();
        second.inc("a");
        second.dec("a");
        assertThat(second.getMaxKey()).isEmpty();
        assertThat(second.getMinKey()).isEmpty();
        Solution third = new Solution();
        third.inc("x");
        third.inc("y");
        assertThat(third.getMaxKey()).isIn("x", "y");
        assertThat(third.getMinKey()).isIn("x", "y");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isBlank() || !input.contains(",")) {
            System.out.println(USAGE);
            return;
        }
        Solution counter = new Solution();
        for (String raw : input.split(",")) {
            String op = raw.trim();
            if (op.startsWith("inc ")) {
                counter.inc(op.substring(4).trim());
            } else if (op.startsWith("dec ")) {
                counter.dec(op.substring(4).trim());
            } else if (op.equals("max")) {
                System.out.println("max -> " + counter.getMaxKey());
            } else if (op.equals("min")) {
                System.out.println("min -> " + counter.getMinKey());
            } else {
                System.out.println(USAGE);
                return;
            }
        }
    }
}
