package com.jinloes.practice;


import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: add 3 3, add 7 3, add 7 7, count 3 7";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        Solution counter = new Solution();
        add(counter, new int[]{3, 3}, new int[]{7, 3}, new int[]{7, 7});
        check(counter, 3, 7, 1);
    }

    private static void example2() {
        Solution counter = new Solution();
        add(counter, new int[]{0, 0}, new int[]{0, 2}, new int[]{2, 0}, new int[]{2, 2}, new int[]{2, 2});
        check(counter, 0, 0, 2);
    }

    private static void example3() {
        Solution counter = new Solution();
        check(counter, 5, 5, 0);
        counter.add(5, 5);
        check(counter, 5, 5, 0);
    }

    private static void add(Solution counter, int[]... points) {
        for (int[] point : points) {
            counter.add(point[0], point[1]);
        }
    }

    private static void check(Solution counter, int x, int y, int expected) {
        assertThat(counter.count(x, y)).as("count(%d, %d)".formatted(x, y)).isEqualTo(expected);
    }

    private static void runInput(String input) {
        Solution counter = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            String[] parts = operation.split("\\s+");
            try {
                if (parts.length != 3) {
                    throw new IllegalArgumentException("operands");
                }
                int x = Integer.parseInt(parts[1]);
                int y = Integer.parseInt(parts[2]);
                if (x < 0 || x > 1000 || y < 0 || y > 1000) {
                    throw new IllegalArgumentException("range");
                }
                switch (parts[0]) {
                    case "add" -> {
                        counter.add(x, y);
                        System.out.println("add(" + x + ", " + y + ")");
                    }
                    case "count" -> System.out.println("count(" + x + ", " + y + ") = " + counter.count(x, y));
                    default -> throw new IllegalArgumentException("operation");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Could not read '" + operation + "'. " + USAGE);
                return;
            }
        }
    }
}
