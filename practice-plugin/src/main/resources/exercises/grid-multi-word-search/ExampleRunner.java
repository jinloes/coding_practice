package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: grid rows separated by commas, then ';' and the words separated by commas, for example: cat,ors,den; cat,cod,care";

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
        assertThat(Solution.findWords(grid("cat", "ors", "den"), new String[]{"cat", "cod", "care", "dog"}))
                .containsExactlyInAnyOrder("cat", "cod", "care");
    }

    private static void example2() {
        assertThat(Solution.findWords(grid("ab"), new String[]{"aba"})).isEmpty();
    }

    private static void example3() {
        assertThat(Solution.findWords(grid("x"), new String[]{"x", "x"})).containsExactly("x");
    }

    private static void runInput(String input) {
        String[] parts = input.split(";");
        if (parts.length != 2) {
            System.out.println(USAGE);
            return;
        }
        String[] rows = parts[0].trim().split("\\s*,\\s*");
        String[] words = parts[1].trim().split("\\s*,\\s*");
        boolean valid = rows.length > 0 && rows[0].matches("[a-z]+");
        for (String row : rows) {
            valid &= row.matches("[a-z]+") && row.length() == rows[0].length();
        }
        for (String word : words) {
            valid &= word.matches("[a-z]+");
        }
        if (!valid) {
            System.out.println("Rows must be equal-length lowercase strings and words must be lowercase. " + USAGE);
            return;
        }
        List<String> found = new ArrayList<>(Solution.findWords(grid(rows), words));
        Collections.sort(found);
        System.out.println("findWords = " + found);
    }

    private static char[][] grid(String... rows) {
        char[][] grid = new char[rows.length][];
        for (int i = 0; i < rows.length; i++) {
            grid[i] = rows[i].toCharArray();
        }
        return grid;
    }
}
