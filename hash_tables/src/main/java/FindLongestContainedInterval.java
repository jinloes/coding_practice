import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Write a program which takes as input a set of integers represented by an array, and returns the size of a largest
 * subset of integers in the array having the property that if two integers are in the subset, then so are all
 * integers between them.
 */
public class FindLongestContainedInterval {
    public static int getLength(List<Integer> toSearch) {
        Set<Integer> notInlcuded = new HashSet<>(toSearch);

        int maxInterval = Integer.MIN_VALUE;

        while (!notInlcuded.isEmpty()) {
            int val = notInlcuded.iterator().next();
            notInlcuded.remove(val);

            int lower = val - 1;
            while (notInlcuded.contains(lower)) {
                notInlcuded.remove(lower);
                lower--;
            }

            int upper = val + 1;

            while (notInlcuded.contains(upper)) {
                notInlcuded.remove(upper);
                upper++;
            }

            maxInterval = Math.max(upper - lower - 1, maxInterval);
        }

        return maxInterval;
    }
}
