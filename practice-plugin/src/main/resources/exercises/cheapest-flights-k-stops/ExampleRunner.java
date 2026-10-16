package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: city count, source, target, max stops, then flights as from>to:price, for example: 4 0 3 1 0>1:100 1>2:100 2>3:100 0>2:500";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        int[][] flights = {{0, 1, 100}, {1, 2, 100}, {2, 3, 100}, {0, 2, 500}};
        assertThat(Solution.cheapestPrice(4, flights, 0, 3, 1)).as("Example 1").isEqualTo(600);
        assertThat(Solution.cheapestPrice(4, flights, 0, 3, 2)).as("Example 2").isEqualTo(300);
        assertThat(Solution.cheapestPrice(4, flights, 0, 2, 0)).as("Example 3").isEqualTo(500);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] tokens = input.split("\\s+");
            int cities = Integer.parseInt(tokens[0]);
            int source = Integer.parseInt(tokens[1]);
            int target = Integer.parseInt(tokens[2]);
            int stops = Integer.parseInt(tokens[3]);
            if (cities < 1 || source < 0 || source >= cities || target < 0 || target >= cities || stops < 0) {
                throw new IllegalArgumentException();
            }
            int[][] flights = new int[tokens.length - 4][];
            for (int i = 4; i < tokens.length; i++) {
                String[] parts = tokens[i].split("[>:]");
                if (parts.length != 3) {
                    throw new IllegalArgumentException();
                }
                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                int price = Integer.parseInt(parts[2]);
                if (from < 0 || from >= cities || to < 0 || to >= cities || price < 0) {
                    throw new IllegalArgumentException();
                }
                flights[i - 4] = new int[]{from, to, price};
            }
            System.out.println("cheapestPrice(" + source + " -> " + target + ", at most " + stops + " stops) = "
                    + Solution.cheapestPrice(cities, flights, source, target, stops));
        } catch (RuntimeException exception) {
            System.out.println("Could not parse input. " + USAGE);
        }
    }
}
