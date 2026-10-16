package com.jinloes.practice;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void twoQueensHaveNoBoard() {
        checkValid(2, 0);
    }

    @Test
    void fiveQueensHaveTenBoards() {
        checkValid(5, 10);
    }

    @Test
    void sixQueensHaveFourBoards() {
        checkValid(6, 4);
    }

    @Test
    void sevenQueensHaveFortyBoards() {
        checkValid(7, 40);
    }

    @Test
    void eightQueensHaveNinetyTwoBoards() {
        checkValid(8, 92);
    }

    @Test
    void zeroIsRejected() {
        assertThatThrownBy(() -> Solution.solveQueens(0)).as("solveQueens(0)").isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void negativeIsRejected() {
        assertThatThrownBy(() -> Solution.solveQueens(-3)).as("solveQueens(-3)").isInstanceOf(IllegalArgumentException.class);
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
}
