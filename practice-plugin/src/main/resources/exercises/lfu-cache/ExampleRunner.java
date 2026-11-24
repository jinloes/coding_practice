package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: cap C; op1, op2, ... where ops are put k v | get k, for example: cap 2; put 1 10, put 2 20, get 1, put 3 30, get 2, get 3";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution c1 = new Solution(2);
        c1.put(1, 10);
        c1.put(2, 20);
        assertThat(c1.get(1)).as("Example 1 get 1").isEqualTo(10);
        c1.put(3, 30);
        assertThat(c1.get(2)).as("Example 1 evicted 2").isEqualTo(-1);
        assertThat(c1.get(3)).as("Example 1 get 3").isEqualTo(30);

        Solution c2 = new Solution(2);
        c2.put(1, 1);
        c2.put(2, 2);
        c2.put(3, 3);
        assertThat(c2.get(1)).as("Example 2 evicted 1").isEqualTo(-1);
        assertThat(c2.get(2)).as("Example 2 get 2").isEqualTo(2);

        Solution c3 = new Solution(0);
        c3.put(1, 1);
        assertThat(c3.get(1)).as("Example 3 zero capacity").isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'cap C; ops'. " + USAGE);
            return;
        }
        String[] header = sections[0].trim().split("\\s+");
        if (header.length != 2 || !"cap".equals(header[0])) {
            System.out.println("Start with 'cap C'. " + USAGE);
            return;
        }
        try {
            int capacity = Integer.parseInt(header[1]);
            if (capacity < 0) {
                System.out.println("Capacity must be non-negative. " + USAGE);
                return;
            }
            Solution cache = new Solution(capacity);
            List<String> outputs = new ArrayList<>();
            for (String raw : sections[1].split(",")) {
                String op = raw.trim();
                if (op.isEmpty()) {
                    continue;
                }
                String[] parts = op.split("\\s+");
                switch (parts[0]) {
                    case "put" -> {
                        if (parts.length != 3) {
                            System.out.println("Invalid put op. " + USAGE);
                            return;
                        }
                        cache.put(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    }
                    case "get" -> {
                        if (parts.length != 2) {
                            System.out.println("Invalid get op. " + USAGE);
                            return;
                        }
                        outputs.add("get=" + cache.get(Integer.parseInt(parts[1])));
                    }
                    default -> {
                        System.out.println("Unknown op '" + parts[0] + "'. " + USAGE);
                        return;
                    }
                }
            }
            System.out.println(String.join(", ", outputs));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer argument. " + USAGE);
        }
    }
}
