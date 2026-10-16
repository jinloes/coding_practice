package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: k, then the points as (x,y) pairs, for example: 2; (3,3) (1,-2) (-1,0) (5,1)";
    private static final Pattern POINT = Pattern.compile("\\(\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*\\)");

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(sortedByDistance(Solution.closest(new int[][]{{3, 3}, {1, -2}, {-1, 0}, {5, 1}}, 2)))
                .as("Example 1").containsExactly(List.of(-1, 0), List.of(1, -2));
        assertThat(sortedByDistance(Solution.closest(new int[][]{{4, -7}}, 1)))
                .as("Example 2").containsExactly(List.of(4, -7));
        assertThat(sortedByDistance(Solution.closest(new int[][]{{2, 2}, {-2, 1}, {0, 5}}, 3)))
                .as("Example 3").containsExactly(List.of(-2, 1), List.of(2, 2), List.of(0, 5));
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int k;
        int[][] points;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("expected k and points");
            }
            k = Integer.parseInt(parts[0].trim());
            points = parsePoints(parts[1]);
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        try {
            System.out.println("closest = " + sortedByDistance(Solution.closest(points, k)));
        } catch (IllegalArgumentException exception) {
            System.out.println("Rejected: " + exception.getMessage());
        }
    }

    private static int[][] parsePoints(String text) {
        List<int[]> points = new ArrayList<>();
        Matcher matcher = POINT.matcher(text);
        int end = 0;
        while (matcher.find()) {
            if (!text.substring(end, matcher.start()).isBlank()) {
                throw new IllegalArgumentException("unexpected text");
            }
            points.add(new int[]{Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2))});
            end = matcher.end();
        }
        if (!text.substring(end).isBlank()) {
            throw new IllegalArgumentException("unexpected text");
        }
        return points.toArray(new int[0][]);
    }

    private static List<List<Integer>> sortedByDistance(int[][] points) {
        return Arrays.stream(points)
                .sorted(Comparator.comparingLong((int[] p) -> (long) p[0] * p[0] + (long) p[1] * p[1]))
                .map(p -> List.of(p[0], p[1]))
                .toList();
    }
}
