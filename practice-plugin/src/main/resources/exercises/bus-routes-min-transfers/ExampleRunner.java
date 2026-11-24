package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: r1 | r2 | ...; source -> target (stops space-separated per route), for example: 1 2 7 | 3 6 7; 1 -> 6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.minBuses(new int[][]{{1, 2, 7}, {3, 6, 7}}, 1, 6))
                .as("Example 1").isEqualTo(2);
        assertThat(solution.minBuses(
                new int[][]{{7, 12}, {4, 5, 15}, {6}, {15, 19}, {9, 12, 13}}, 15, 12))
                .as("Example 2").isEqualTo(-1);
        assertThat(solution.minBuses(new int[][]{{1, 2, 3}}, 5, 5))
                .as("Example 3").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'routes; source -> target'. " + USAGE);
            return;
        }
        String[] endpoints = sections[1].split("->", -1);
        if (endpoints.length != 2) {
            System.out.println("Provide 'source -> target'. " + USAGE);
            return;
        }
        try {
            int source = Integer.parseInt(endpoints[0].trim());
            int target = Integer.parseInt(endpoints[1].trim());
            String routesText = sections[0].trim();
            int[][] routes;
            if (routesText.isEmpty()) {
                routes = new int[0][];
            } else {
                String[] rawRoutes = routesText.split("\\|");
                routes = new int[rawRoutes.length][];
                for (int i = 0; i < rawRoutes.length; i++) {
                    String[] stops = rawRoutes[i].trim().split("\\s+");
                    routes[i] = new int[stops.length];
                    for (int j = 0; j < stops.length; j++) {
                        routes[i][j] = Integer.parseInt(stops[j]);
                    }
                }
            }
            System.out.println(new Solution().minBuses(routes, source, target));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer stop ID. " + USAGE);
        }
    }
}
