package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: board <width> <height>; food <r,c ... or ->; moves <U|D|L|R ...>, for example: board 3 2; food 1,2 0,1; moves R D R U L U";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution(3, 2, new int[][]{{1, 2}, {0, 1}});
        assertThat(steps(first, 'R', 'D', 'R', 'U', 'L', 'U'))
                .as("Example 1")
                .containsExactly(0, 0, 1, 1, 2, -1);

        Solution second = new Solution(2, 2, new int[][]{{0, 1}, {1, 1}, {1, 0}});
        assertThat(steps(second, 'R', 'D', 'L', 'U', 'R'))
                .as("Example 2")
                .containsExactly(1, 2, 3, 3, 3);

        Solution third = new Solution(3, 3, new int[][]{{0, 1}, {0, 2}, {1, 2}, {1, 1}});
        assertThat(steps(third, 'R', 'R', 'D', 'L', 'U', 'R'))
                .as("Example 3")
                .containsExactly(1, 2, 3, 4, -1, -1);

        System.out.println("Examples passed: 3");
    }

    private static List<Integer> steps(Solution game, char... moves) {
        List<Integer> scores = new ArrayList<>(moves.length);
        for (char move : moves) {
            scores.add(game.step(move));
        }
        return scores;
    }

    private static void runInput(String input) {
        try {
            String[] clauses = input.split(";");
            if (clauses.length != 3) {
                throw new IllegalArgumentException();
            }
            String[] board = clauses[0].trim().split("\\s+");
            if (board.length != 3 || !"board".equals(board[0])) {
                throw new IllegalArgumentException();
            }
            int width = Integer.parseInt(board[1]);
            int height = Integer.parseInt(board[2]);

            String[] foodTokens = clauses[1].trim().split("\\s+");
            if (foodTokens.length < 2 || !"food".equals(foodTokens[0])) {
                throw new IllegalArgumentException();
            }
            int[][] food;
            if (foodTokens.length == 2 && "-".equals(foodTokens[1])) {
                food = new int[0][0];
            } else {
                food = new int[foodTokens.length - 1][2];
                for (int i = 1; i < foodTokens.length; i++) {
                    String[] rc = foodTokens[i].split(",");
                    if (rc.length != 2) {
                        throw new IllegalArgumentException();
                    }
                    food[i - 1][0] = Integer.parseInt(rc[0]);
                    food[i - 1][1] = Integer.parseInt(rc[1]);
                }
            }

            String[] moveTokens = clauses[2].trim().split("\\s+");
            if (moveTokens.length < 2 || !"moves".equals(moveTokens[0])) {
                throw new IllegalArgumentException();
            }
            Solution game = new Solution(width, height, food);
            List<Integer> scores = new ArrayList<>(moveTokens.length - 1);
            for (int i = 1; i < moveTokens.length; i++) {
                if (moveTokens[i].length() != 1) {
                    throw new IllegalArgumentException();
                }
                scores.add(game.step(moveTokens[i].charAt(0)));
            }
            System.out.println("scores = " + scores);
        } catch (RuntimeException exception) {
            System.out.println("Could not read operations. " + USAGE);
        }
    }
}
