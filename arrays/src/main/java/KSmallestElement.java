import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Return k smallest element from an array.
 */
public class KSmallestElement {
    public static int compute(int[] arr, int k) {
        SortedSet<Integer> temp = new TreeSet<>();
        for (int i = 0; i < arr.length; i++) {
            temp.add(arr[i]);
        }
        return temp.subSet(k, k + 1).first();
    }
}
