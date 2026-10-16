package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the fuel gains, a semicolon, then the travel costs, for example: [4, 2, 3]; [3, 4, 2]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        check(new int[]{4, 2, 3}, new int[]{3, 4, 2}, true);
        check(new int[]{1, 2}, new int[]{3, 3}, false);
        check(new int[]{5}, new int[]{5}, true);
        System.out.println("Examples passed: 3");
    }

    private static void check(int[] gain, int[] cost, boolean possible) {
        String call = "startingStation(%s, %s)".formatted(Arrays.toString(gain), Arrays.toString(cost));
        int start = Solution.startingStation(gain, cost);
        if (!possible) {
            assertThat(start).as(call).isEqualTo(-1);
            return;
        }
        assertThat(start).as(call).isBetween(0, gain.length - 1);
        assertThat(completes(gain, cost, start)).as(call + " returned " + start + ", which must finish the circuit").isTrue();
    }

    private static boolean completes(int[] gain, int[] cost, int start) {
        long tank = 0;
        for (int step = 0; step < gain.length; step++) {
            int station = (start + step) % gain.length;
            tank += gain[station] - cost[station];
            if (tank < 0) {
                return false;
            }
        }
        return true;
    }

    private static void runInput(String input) {
        int[] gain;
        int[] cost;
        try {
            String[] parts = input.split(";", -1);
            if (parts.length != 2) {
                throw new IllegalArgumentException("two lists expected");
            }
            gain = parseInts(parts[0]);
            cost = parseInts(parts[1]);
            if (gain.length == 0 || gain.length != cost.length) {
                throw new IllegalArgumentException("lists must have equal, non-zero length");
            }
        } catch (RuntimeException exception) {
            System.out.println("Could not read \"" + input + "\" as two equal-length lists. " + USAGE);
            return;
        }
        System.out.println("startingStation(" + Arrays.toString(gain) + ", " + Arrays.toString(cost) + ") = "
                + Solution.startingStation(gain, cost));
    }

    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) {
            return new int[0];
        }
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            values[i] = Integer.parseInt(tokens[i]);
        }
        return values;
    }
}
