package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: start row, start column, and a one-digit new color, then image rows of digits separated by spaces, for example: 1 1 2 111 110 101";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.recolor(new int[][]{{1, 1, 1}, {1, 1, 0}, {1, 0, 1}}, 1, 1, 2))
                .as("Example 1").isDeepEqualTo(new int[][]{{2, 2, 2}, {2, 2, 0}, {2, 0, 1}});
        assertThat(Solution.recolor(new int[][]{{0, 0}, {0, 0}}, 0, 0, 0))
                .as("Example 2").isDeepEqualTo(new int[][]{{0, 0}, {0, 0}});
        assertThat(Solution.recolor(new int[][]{{1, 2, 1}, {2, 1, 2}, {1, 2, 1}}, 1, 1, 7))
                .as("Example 3").isDeepEqualTo(new int[][]{{1, 2, 1}, {2, 7, 2}, {1, 2, 1}});
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] tokens = input.split("\\s+");
        int[][] image = parse(tokens);
        if (image == null) {
            System.out.println("Could not read \"" + input + "\" as a start pixel, color, and image. " + USAGE);
            return;
        }
        int row = Integer.parseInt(tokens[0]);
        int col = Integer.parseInt(tokens[1]);
        int color = Integer.parseInt(tokens[2]);
        String call = "recolor(%s, %d, %d, %d)".formatted(Arrays.deepToString(image), row, col, color);
        System.out.println(call + " = " + Arrays.deepToString(Solution.recolor(image, row, col, color)));
    }

    private static int[][] parse(String[] tokens) {
        if (tokens.length < 4 || !tokens[0].matches("\\d{1,4}") || !tokens[1].matches("\\d{1,4}")
                || !tokens[2].matches("\\d")) {
            return null;
        }
        int width = tokens[3].length();
        int[][] image = new int[tokens.length - 3][];
        for (int r = 0; r < image.length; r++) {
            String row = tokens[r + 3];
            if (!row.matches("\\d+") || row.length() != width) {
                return null;
            }
            image[r] = row.chars().map(digit -> digit - '0').toArray();
        }
        if (Integer.parseInt(tokens[0]) >= image.length || Integer.parseInt(tokens[1]) >= width) {
            return null;
        }
        return image;
    }
}
