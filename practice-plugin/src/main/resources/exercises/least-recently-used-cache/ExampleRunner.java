package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: capacity first, then operations separated by commas, for example: capacity 2, put 1 10, put 2 20, get 1, put 3 30, get 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution cache = new Solution(2);
        cache.put(1, 10);
        cache.put(2, 20);
        assertThat(cache.get(1)).as("Example 1: get(1)").isEqualTo(10);
        cache.put(3, 30);
        assertThat(cache.get(2)).as("Example 1: key 2 is evicted").isEqualTo(-1);

        Solution single = new Solution(1);
        single.put(1, 5);
        single.put(2, 6);
        assertThat(single.get(1)).as("Example 2: key 1 is evicted").isEqualTo(-1);
        assertThat(single.get(2)).as("Example 2: get(2)").isEqualTo(6);

        Solution overwrite = new Solution(2);
        overwrite.put(1, 1);
        overwrite.put(2, 2);
        overwrite.put(1, 100);
        overwrite.put(3, 3);
        assertThat(overwrite.get(1)).as("Example 3: overwritten value").isEqualTo(100);
        assertThat(overwrite.get(2)).as("Example 3: key 2 is evicted").isEqualTo(-1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] operations = input.split(",");
        Solution cache = null;
        try {
            String[] first = operations[0].trim().split("\\s+");
            if (first.length != 2 || !first[0].equals("capacity")) {
                System.out.println("The first operation must set the capacity. " + USAGE);
                return;
            }
            cache = new Solution(Integer.parseInt(first[1]));
            System.out.println("capacity " + first[1]);
            for (int i = 1; i < operations.length; i++) {
                String operation = operations[i].trim();
                if (operation.isEmpty()) {
                    continue;
                }
                String[] parts = operation.split("\\s+");
                if (parts[0].equals("get") && parts.length == 2) {
                    int key = Integer.parseInt(parts[1]);
                    System.out.println("get(" + key + ") = " + cache.get(key));
                } else if (parts[0].equals("put") && parts.length == 3) {
                    int key = Integer.parseInt(parts[1]);
                    int value = Integer.parseInt(parts[2]);
                    cache.put(key, value);
                    System.out.println("put(" + key + ", " + value + ")");
                } else {
                    System.out.println("Unknown operation '" + operation + "'. " + USAGE);
                    return;
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("Could not parse input. " + USAGE);
        } catch (IllegalArgumentException exception) {
            System.out.println("Capacity must be at least 1. " + USAGE);
        }
    }
}
