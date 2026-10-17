package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: capacity first, then operations separated by commas, for example: capacity 4, put a 1, get a, size";

    private ExampleRunner() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution(4);
        first.put("a", "1");
        assertThat(first.get("a")).isEqualTo("1");
        assertThat(first.size()).isEqualTo(1);
        Solution second = new Solution(4);
        second.put("a", "1");
        second.put("a", "2");
        assertThat(second.get("a")).isEqualTo("2");
        assertThat(second.size()).isEqualTo(1);
        Solution third = new Solution(2);
        third.put("a", "1");
        assertThat(third.remove("a")).isTrue();
        assertThat(third.remove("a")).isFalse();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] parts = input.split(",");
        if (parts.length == 0 || !parts[0].trim().startsWith("capacity ")) {
            System.out.println(USAGE);
            return;
        }
        Solution table;
        try {
            table = new Solution(Integer.parseInt(parts[0].trim().substring(9).trim()));
        } catch (RuntimeException exception) {
            System.out.println(USAGE);
            return;
        }
        for (int i = 1; i < parts.length; i++) {
            String op = parts[i].trim();
            String[] tokens = op.split("\s+");
            if (tokens.length == 3 && tokens[0].equals("put")) {
                table.put(tokens[1], tokens[2]);
            } else if (tokens.length == 2 && tokens[0].equals("get")) {
                System.out.println("get " + tokens[1] + " -> " + table.get(tokens[1]));
            } else if (tokens.length == 2 && tokens[0].equals("remove")) {
                System.out.println("remove " + tokens[1] + " -> " + table.remove(tokens[1]));
            } else if (tokens.length == 1 && tokens[0].equals("size")) {
                System.out.println("size -> " + table.size());
            } else {
                System.out.println(USAGE);
                return;
            }
        }
    }
}
