import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Write a program which takes an array of strings and a set of strings, and return the indices of the starting and
 * ending index of a shortest subarray of the given array that "covers" the set, i.e.,
 * contains all strings in the set.
 */
public class FindSmallestSubarray {

    public static List<Integer> shortestSubarray(List<String> toCheck, Set<String> toCover) {
        Map<String, Integer> keywordsToCover = new HashMap<>();
        for (String keyword : toCover) {
            keywordsToCover.put(keyword, keywordsToCover.getOrDefault(keyword, 0) + 1);
        }

        int remainingKeywords = toCover.size();

        int left = 0;
        int right = 0;

        int minLeft = -1;
        int minRight = -1;

        while (right < toCheck.size()) {
            String current = toCheck.get(right);
            Integer count = keywordsToCover.get(current);

            if (count != null) {
                count--;
                keywordsToCover.put(current, count);
                if (count >= 0) {
                    remainingKeywords--;
                }
            }

            while (remainingKeywords == 0) {
                if (minLeft == -1 || right - left < minRight - minLeft) {
                    minLeft = left;
                    minRight = right;

                }
                String toAdd = toCheck.get(left);
                Integer toAddCount = keywordsToCover.get(toAdd);

                if (toAddCount != null) {
                    toAddCount++;
                    keywordsToCover.put(toAdd, toAddCount);
                    if (toAddCount > 0) {
                        remainingKeywords++;
                    }
                }
                left++;
            }
            right++;
        }

        return List.of(minLeft, minRight);
    }
}
