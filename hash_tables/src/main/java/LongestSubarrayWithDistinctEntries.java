import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Write a program that takes an array and returns the length of a longest subarray with the property that all
 * its elements are distinct.
 */
public class LongestSubarrayWithDistinctEntries {
    public static int get(List<Character> entries) {
        int left = 0;
        int right = 0;

        int maxDistance = Integer.MIN_VALUE;

        Set<Character> seen = new HashSet<>();

        while (right < entries.size()) {
            char current = entries.get(right);

            while (seen.contains(current)) {
                seen.remove(entries.get(left));
                left++;
            }

            seen.add(current);
            if (right - left > maxDistance) {
                maxDistance = right - left;
            }
            right++;
        }

        return maxDistance + 1;
    }
}
