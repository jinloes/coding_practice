import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Write a program which takes as input a string (the "sentence") and an array of strings (the "words"), and returns
 * the starting indices of substrings of the sentence string which are the concatenation of all the strings in the
 * words array. Each string must appear exactly once, and their ordering is immaterial.
 * <p>
 * Assume all strings in the words array have equal length. It is possible for the words array to contain duplicates.
 */
// O(Nmn)
public class ComputeStringDecompositions {
    public static List<Integer> findAHSubstrings(String s, List<String> words) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        int wordSize = words.get(0).length();

        List<Integer> result = new ArrayList<>();

        // O(N)
        for (int i = 0; i + wordSize * words.size() <= s.length(); i++) {
            if (matchAgainstWordsinDict(s, wordCount, i, words.size(), wordSize)) {
                result.add(i);
            }
        }

        return result;
    }

    public static boolean matchAgainstWordsinDict(String s, Map<String, Integer> wordCount, int start, int numWords,
                                                  int wordSize) {
        Map<String, Integer> currentMatch = new HashMap<>();

        // O(m)
        for (int i = 0; i < numWords; i++) {
            // O(n)
            String current = s.substring(start + i, start + i + wordSize);

            if (!wordCount.containsKey(current)) {
                return false;
            }

            int newCount = currentMatch.put(current, currentMatch.getOrDefault(current, 0) + 1);

            int expectedMatch = wordCount.get(current);

            if (newCount > expectedMatch) {
                return false;
            }
        }

        return true;

    }
}
