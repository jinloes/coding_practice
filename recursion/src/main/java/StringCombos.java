import java.util.*;

/**
 * Implement a function that prints all possible combinations of the characters
 * in a string. These combinations range in length from one to the length of the
 * string. Two combinations that differ only in ordering of their characters are
 * the same combination.
 */
public class StringCombos {
    public static List<String> generate(String str) {
        List<String> result = new ArrayList<>();

        helper(str, 0, new StringBuilder(), result);

        return result;
    }

    private static void helper(String str, int start, StringBuilder current, List<String> result) {
        for (int i = start; i < str.length(); i++) {
            current.append(str.charAt(i));
            result.add(current.toString());
            helper(str, i + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }
}