import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Write a program which takes as input two sorted arrays, and returns a new array containing elements that are
 * present in both of the input arrays.
 * <p>
 * The input arrays may have duplicate entries, but the returned array should be free of duplicates.
 */
public class SortedArraysIntersection {
    public static List<Integer> getIntersection(List<Integer> list1, List<Integer> list2) {
        Set<Integer> result = new HashSet<>();


        int i = 0;
        int j = 0;

        while (i < list1.size() && j < list2.size()) {
            int val1 = list1.get(i);
            int val2 = list2.get(j);
            if (val1 == val2) {
                result.add(list1.get(i));
                i++;
                j++;
            } else if (val1 > val2) {
                j++;
            } else {
                i++;
            }
        }

        return new ArrayList<>(result);
    }
}
