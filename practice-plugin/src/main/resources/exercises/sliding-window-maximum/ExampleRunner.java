package com.jinloes.practice;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner {
    private static final String USAGE = "Input: the values followed by window size, for example: [1, 3, -1, -3, 5, 3, 6, 7] 3";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.maxSlidingWindow(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3)).containsExactly(3, 3, 5, 5, 6, 7);
        assertThat(Solution.maxSlidingWindow(new int[]{1}, 1)).containsExactly(1);
        assertThat(Solution.maxSlidingWindow(new int[]{9, 8, 7, 6}, 2)).containsExactly(9, 8, 7);
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try {
            int[] values = parseInts(input);
            if (values.length < 2) { System.out.println("Need values and a window size. " + USAGE); return; }
            int window = values[values.length - 1];
            int[] numbers = Arrays.copyOf(values, values.length - 1);
            System.out.println("maxSlidingWindow(" + Arrays.toString(numbers) + ", " + window + ") = "
                    + Arrays.toString(Solution.maxSlidingWindow(numbers, window)));
        } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); }
    }
    private static int[] parseInts(String input) {
        String cleaned = input.replace('[', ' ').replace(']', ' ').replace(',', ' ').trim();
        if (cleaned.isEmpty()) return new int[0];
        String[] tokens = cleaned.split("\\s+");
        int[] values = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) values[i] = Integer.parseInt(tokens[i]);
        return values;
    }
}
