import java.util.*;

/**
 * Write a function that takes a seven-digit telephone number and prints out all
 * of the possible “words” or combinations of letters that can represent the
 * given number. Because the 0 and 1 keys have no letters on them, you should
 * change only the digits 2–9 to letters. You’ll be passed an array of seven
 * integers, with each element being one digit in the number. You may assume
 * that only valid phone numbers will be passed to your function.
 */
public class TelephoneWords {
    private static final Map<Character, Set<Character>> NUM_TO_CHARS = new HashMap<>();

    static {
        NUM_TO_CHARS.put('2', Set.of('A', 'B', 'C'));
        NUM_TO_CHARS.put('3', Set.of('D', 'E', 'F'));
        NUM_TO_CHARS.put('4', Set.of('G', 'H', 'I'));
        NUM_TO_CHARS.put('5', Set.of('J', 'K', 'L'));
        NUM_TO_CHARS.put('6', Set.of('M', 'N', 'O'));
        NUM_TO_CHARS.put('7', Set.of('P', 'R', 'S'));
        NUM_TO_CHARS.put('8', Set.of('T', 'U', 'V'));
        NUM_TO_CHARS.put('9', Set.of('X', 'Y', 'Z'));
    }

    public static List<String> generate(String phoneNumber) {
        List<String> result = new ArrayList<>();

        helper(phoneNumber, 0, new StringBuilder(), result);
        return result;
    }

    private static void helper(String phoneNumber, int idx, StringBuilder current, List<String> result) {
        if (idx == phoneNumber.length()) {
            result.add(current.toString());
            return;
        }

        Set<Character> chars = NUM_TO_CHARS.get(phoneNumber.charAt(idx));

        for (Character c : chars) {
            current.append(c);
            helper(phoneNumber, idx + 1, current, result);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
