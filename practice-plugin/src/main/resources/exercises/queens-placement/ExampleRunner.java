package com.jinloes.practice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: the board size n, for example: 6";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        checkFourQueens();
        assertThat(Solution.solveQueens(1)).as("solveQueens(1)").containsExactly(List.of("Q"));
        checkValid(3, 0);
        System.out.println("Examples passed: 3");
    }

    private static void checkValid(int n, int expectedCount) {
        String call = "solveQueens(" + n + ")";
        List<List<String>> actual = Solution.solveQueens(n);
        assertThat(actual).as(call).isNotNull().hasSize(expectedCount);
        assertThat(new HashSet<>(actual)).as(call + " distinct boards").hasSize(expectedCount);
        for (List<String> board : actual) {
            assertThat(board).as(call + " rows of " + board).hasSize(n);
            Set<Integer> columns = new HashSet<>();
            Set<Integer> down = new HashSet<>();
            Set<Integer> up = new HashSet<>();
            for (int r = 0; r < n; r++) {
                String row = board.get(r);
                assertThat(row).as(call + " row " + r + " of " + board).hasSize(n).matches("\\.*Q\\.*");
                int c = row.indexOf('Q');
                columns.add(c);
                down.add(r - c);
                up.add(r + c);
            }
            assertThat(columns).as(call + " columns of " + board).hasSize(n);
            assertThat(down).as(call + " diagonals of " + board).hasSize(n);
            assertThat(up).as(call + " anti-diagonals of " + board).hasSize(n);
        }
    }

    private static void checkFourQueens() {
        checkValid(4, 2);
        assertThat(Solution.solveQueens(4)).as("solveQueens(4)").containsExactlyInAnyOrder(
                List.of(".Q..", "...Q", "Q...", "..Q."),
                List.of("..Q.", "Q...", "...Q", ".Q.."));
    }

    private static void runInput(String input) {
        int n;
        try {
            n = Integer.parseInt(input.trim());
        } catch (NumberFormatException exception) {
            System.out.println("Could not read \"" + input + "\" as a board size. " + USAGE);
            return;
        }
        if (n < 1 || n > 10) {
            System.out.println("The board size must be between 1 and 10. " + USAGE);
            return;
        }
        List<List<String>> boards = Solution.solveQueens(n);
        System.out.println("solveQueens(" + n + ") found " + boards.size() + " boards: " + boards);
    }
}
