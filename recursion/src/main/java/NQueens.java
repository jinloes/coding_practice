import java.util.ArrayList;
import java.util.List;

/**
 * Write a program which returns all distinct nonattacking placements of n queens on an nxn chessboard, where n is an
 * input to the program.
 */
public class NQueens {
    public static List<List<Integer>> solve(int n) {
        List<List<Integer>> result = new ArrayList<>();

        solve(n, 0, new ArrayList<>(), result);

        return result;
    }

    private static void solve(int n, int row, List<Integer> columns, List<List<Integer>> result) {
        if (row == n) {
            result.add(new ArrayList<>(columns));
            return;
        }

        for (int column = 0; column < n; column++) {
            columns.add(column);
            if (isValid(columns)) {
                solve(n, row + 1, columns, result);
            }
            columns.remove(columns.size() - 1);
        }
    }

    private static boolean isValid(List<Integer> columns) {
        int row = columns.size() - 1;

        for (int i = 0; i < row; i++) {
            // column check, no other values in the list should be equal
            if (columns.get(i).equals(columns.get(row))) {
                return false;
            }
            // need to do diagonal check
            // up right
            for (int j = 1; row + j < columns.size(); j++) {
                int currentRow = row - j;
                if (columns.get(currentRow) == columns.get(row) + j) {
                    return false;
                }
            }

            // up left
            for (int j = 1; row + j < columns.size(); j++) {
                int currentRow = row - j;
                if (columns.get(currentRow) == columns.get(row) - j) {
                    return false;
                }
            }

            // down left
            for (int j = 1; row + j < columns.size(); j++) {
                int currentRow = row + j;
                if (columns.get(currentRow) == columns.get(row) - j) {
                    return false;
                }
            }

            // down right
            for (int j = 1; row + j < columns.size(); j++) {
                int currentRow = row + j;
                if (columns.get(currentRow) == columns.get(row) + j) {
                    return false;
                }
            }

            //int diff = Math.abs(columns.get(i) - columns.get(row));
            /*if (diff == 0 || diff == row - i) {
                return false;
            }*/
        }
        return true;
    }

}
