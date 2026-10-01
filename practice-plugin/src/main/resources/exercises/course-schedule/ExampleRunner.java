package com.jinloes.practice;
import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;
public final class ExampleRunner {
    private static final String USAGE = "Input: course count, then prerequisite pairs as course:prerequisite, for example: 4 1:0 2:1 3:2";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.canFinish(2, new int[][]{{1, 0}})).isTrue();
        assertThat(Solution.canFinish(2, new int[][]{{1, 0}, {0, 1}})).isFalse();
        assertThat(Solution.canFinish(4, new int[][]{{1, 0}, {2, 1}, {3, 2}})).isTrue();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try {
            String[] tokens = input.trim().split("\\s+");
            if (tokens.length == 0 || tokens[0].isEmpty()) { System.out.println("Need course data. " + USAGE); return; }
            int count = Integer.parseInt(tokens[0]);
            int[][] pairs = new int[tokens.length - 1][2];
            for (int i = 1; i < tokens.length; i++) {
                String[] pair = tokens[i].split(":");
                if (pair.length != 2) throw new NumberFormatException();
                pairs[i - 1] = new int[]{Integer.parseInt(pair[0]), Integer.parseInt(pair[1])};
            }
            System.out.println("canFinish(" + count + ", " + Arrays.deepToString(pairs) + ") = "
                    + Solution.canFinish(count, pairs));
        } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); }
    }
}
