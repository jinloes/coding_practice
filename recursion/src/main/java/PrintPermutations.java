import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Implement a routine that prints all possible orderings of the characters in a
 * string.
 */
public class PrintPermutations {
    public static List<String> generate(String chars) {
        List<String> result = new ArrayList<>();
        Set<Character> charSet = new HashSet<>();
        for (char c : chars.toCharArray()) {
            charSet.add(c);
        }

        generateHelper(charSet, new StringBuilder(), new HashSet<>(), result);

        return result;
    }

    private static void generateHelper(Set<Character> chars, StringBuilder current, Set<Character> used,
                                       List<String> result) {
        if (chars.size() == used.size()) {
            result.add(current.toString());
            return;
        }

        for (Character c : chars) {
            if (!used.contains(c)) {
                current.append(c);
                used.add(c);
                generateHelper(chars, current, used, result);
                current.deleteCharAt(current.length() - 1);
                used.remove(c);
            }
        }
    }
}
