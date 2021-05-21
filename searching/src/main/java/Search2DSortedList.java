import java.util.List;

/**
 * Design an algorithm that takes a 2D sorted array and a number and checks whether that number appears in the array.
 */
public class Search2DSortedList {
    public static boolean search(List<List<Integer>> toSearch, int val) {
        int row = 0;
        int col = toSearch.get(0).size() - 1;

        while (row < toSearch.size() && col >= 0) {
            if (toSearch.get(row).get(col) == val) {
                return true;
            } else if (toSearch.get(row).get(col) < val) {
                row++;
            } else if (toSearch.get(row).get(col) > val) {
                col--;
            }
        }

        return false;
    }
}
