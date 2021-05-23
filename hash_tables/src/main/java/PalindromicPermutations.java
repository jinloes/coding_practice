import java.util.HashMap;
import java.util.Map;

/**
 * Write a program to test whether the letters forming a string can be permuted to form a palindrome.
 */
public class PalindromicPermutations {
    public static boolean test(String str) {
        Map<Character, Integer> charCounts = new HashMap<>();

        for (char c : str.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }

        int numOdd = 0;

        for (int count : charCounts.values()) {
            if (count % 2 == 1) {
                numOdd++;
            }
        }

        return numOdd <= 1;
    }
}
