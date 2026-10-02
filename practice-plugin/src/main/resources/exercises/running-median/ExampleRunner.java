package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: add 5, add 15, median, add 1, median";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution stream = new Solution();
        stream.add(5);
        assertThat(stream.median()).as("Example 1: median of [5]").isEqualTo(5.0);
        stream.add(15);
        assertThat(stream.median()).as("Example 1: median of [5, 15]").isEqualTo(10.0);
        stream.add(1);
        assertThat(stream.median()).as("Example 1: median of [5, 15, 1]").isEqualTo(5.0);

        Solution empty = new Solution();
        assertThatThrownBy(empty::median).isInstanceOf(IllegalStateException.class);

        Solution repeated = new Solution();
        repeated.add(-1);
        repeated.add(-1);
        assertThat(repeated.median()).as("Example 3: median of [-1, -1]").isEqualTo(-1.0);
        repeated.add(4);
        assertThat(repeated.median()).as("Example 3: median of [-1, -1, 4]").isEqualTo(-1.0);
        repeated.add(4);
        assertThat(repeated.median()).as("Example 3: median of [-1, -1, 4, 4]").isEqualTo(1.5);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution stream = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            if (!apply(stream, operation)) {
                return;
            }
        }
    }

    private static boolean apply(Solution stream, String operation) {
        String[] parts = operation.split("\\s+");
        try {
            switch (parts[0]) {
                case "add" -> {
                    if (parts.length != 2) {
                        System.out.println("'" + operation + "' needs exactly one value. " + USAGE);
                        return false;
                    }
                    int value = Integer.parseInt(parts[1]);
                    stream.add(value);
                    System.out.println("add(" + value + ")");
                }
                case "median" -> System.out.println("median() = " + stream.median());
                default -> {
                    System.out.println("Unknown operation '" + operation + "'. " + USAGE);
                    return false;
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("'" + operation + "' does not name an integer. " + USAGE);
            return false;
        } catch (IllegalStateException exception) {
            System.out.println("median() threw IllegalStateException because no value has been added");
        }
        return true;
    }
}
