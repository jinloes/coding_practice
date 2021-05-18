import java.util.Arrays;

/**
 * Check whether a 9 X 9 2D array representing a partially completed Sudoku is valid. Specifically, check that no
 * row, column, or 3 X 3 2D subarray contains duplicates.
 * A 0-value in the 2D array indicates that entry is blank; every other entry is in [1,9].
 */
public class SudokuChecker {

    public static boolean check(int[][] board) {
        boolean[] isValid = new boolean[9];

        // check rows
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                int idx = board[i][j] - 1;
                if (idx < 0 || isValid[idx]) {
                    return false;
                }
                isValid[idx] = true;
            }
            Arrays.fill(isValid, false);
        }

        // check cols
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board.length; j++) {
                int idx = board[j][i] - 1;
                if (idx < 0 || isValid[idx]) {
                    return false;
                }
                isValid[idx] = true;
            }
            Arrays.fill(isValid, false);
        }

        // check squares
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    for (int l = 0; l < 3; l++) {
                        int idx = board[k + (i * 3)][l + (j * 3)] - 1;
                        if (idx < 0 || isValid[idx]) {
                            return false;
                        }
                        isValid[idx] = true;
                    }
                    Arrays.fill(isValid, false);

                }
            }
        }

        return true;
    }
}
