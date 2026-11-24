package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: comma-separated ops 'U ts price', 'L', 'MAX', 'MIN', for example: U 1 10, U 2 5, L, MAX, U 1 3, MAX";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution t1 = new Solution();
        t1.update(1, 10);
        t1.update(2, 5);
        assertThat(t1.latest()).as("Example 1 latest").isEqualTo(5);
        assertThat(t1.maxPrice()).as("Example 1 max 1").isEqualTo(10);
        t1.update(1, 3);
        assertThat(t1.maxPrice()).as("Example 1 max 2").isEqualTo(5);
        t1.update(4, 2);
        assertThat(t1.minPrice()).as("Example 1 min").isEqualTo(2);

        Solution t2 = new Solution();
        t2.update(1, 10);
        t2.update(2, 10);
        t2.update(1, 4);
        assertThat(t2.maxPrice()).as("Example 2 max").isEqualTo(10);
        assertThat(t2.minPrice()).as("Example 2 min").isEqualTo(4);
        assertThat(t2.latest()).as("Example 2 latest").isEqualTo(10);

        Solution t3 = new Solution();
        t3.update(5, 20);
        t3.update(2, 50);
        assertThat(t3.latest()).as("Example 3 latest").isEqualTo(20);
        assertThat(t3.maxPrice()).as("Example 3 max").isEqualTo(50);
        assertThat(t3.minPrice()).as("Example 3 min").isEqualTo(20);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution tracker = new Solution();
        List<String> outputs = new ArrayList<>();
        try {
            for (String raw : input.split(",")) {
                String[] parts = raw.trim().split("\\s+");
                if (parts.length == 3 && "U".equalsIgnoreCase(parts[0])) {
                    tracker.update(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                } else if (parts.length == 1 && "L".equalsIgnoreCase(parts[0])) {
                    outputs.add(String.valueOf(tracker.latest()));
                } else if (parts.length == 1 && "MAX".equalsIgnoreCase(parts[0])) {
                    outputs.add(String.valueOf(tracker.maxPrice()));
                } else if (parts.length == 1 && "MIN".equalsIgnoreCase(parts[0])) {
                    outputs.add(String.valueOf(tracker.minPrice()));
                } else {
                    System.out.println("Invalid op '" + raw.trim() + "'. " + USAGE);
                    return;
                }
            }
            System.out.println(String.join(", ", outputs));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer in op. " + USAGE);
        }
    }
}
