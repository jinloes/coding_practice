import java.util.ArrayList;
import java.util.List;

/**
 * Compute all palindromic decompositions of a given string.
 */
public class PalindromicDecompositions {
    public static List<List<String>> decompose(String str) {
        List<List<String>> result = new ArrayList<>();

        decompose(str, 0, new ArrayList<>(), result);

        return result;
    }

    private static void decompose(String str, int start, List<String> current, List<List<String>> result) {
        if (start == str.length()) {
            result.add(new ArrayList<>(current));
            return;
        }


        for (int i = start + 1; i <= str.length(); i++) {
            String substr = str.substring(start, i);
            if (isPalindrome(substr)) {
                current.add(substr);
                decompose(str, i, current, result);
                current.remove(current.size() - 1);
            }

        }

    }

    private static boolean isPalindrome(String str) {
        for (int i = 0; i < str.length() / 2; i++) {
            if (str.charAt(i) != str.charAt(str.length() - i - 1)) {
                return false;
            }
        }
        return true;
    }

}
