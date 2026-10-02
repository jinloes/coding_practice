package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the grid rows followed by the word, separated by spaces, for example: ABC SED TRO BEST";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        char[][] grid = {{'A', 'B', 'C'}, {'S', 'E', 'D'}, {'T', 'R', 'O'}};
        assertThat(Solution.wordExists(grid, "BEST")).as("Example 1").isTrue();
        assertThat(Solution.wordExists(grid, "BEE")).as("Example 2").isFalse();
        assertThat(Solution.wordExists(new char[][]{{'Z'}}, "Z")).as("Example 3").isTrue();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] tokens = input.split("\\s+");
        if (tokens.length < 2) {
            System.out.println("Need at least one grid row and a word. " + USAGE);
            return;
        }
        char[][] grid = new char[tokens.length - 1][];
        for (int i = 0; i < grid.length; i++) {
            grid[i] = tokens[i].toCharArray();
            if (grid[i].length != grid[0].length) {
                System.out.println("Grid rows must all have the same length. " + USAGE);
                return;
            }
        }
        String word = tokens[tokens.length - 1];
        System.out.println("wordExists(" + String.join(" ", java.util.Arrays.copyOf(tokens, tokens.length - 1)) + ", \"" + word + "\") = "
                + Solution.wordExists(grid, word));
    }

    private static List<String> quoted(String input) {
        Matcher matcher = Pattern.compile("'([^']*)'").matcher(input);
        List<String> found = new ArrayList<>();
        while (matcher.find()) {
            found.add(matcher.group(1));
        }
        return found;
    }
}
