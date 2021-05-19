import com.google.common.collect.ImmutableMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Write a program which takes as input a phone number, specified as a string of digits, and returns all possible
 * character sequences that correspond to the phone number. The cell phone keypad is specified by a mapping that
 * takes a digit and returns the corresponding set of characters.
 * The character sequences do not have to be legal words or phrases.
 */
public class ComputeMnemonics {

    private static Map<Character, Set<Character>> numberToChars = ImmutableMap.<Character, Set<Character>>builder()
            .put('2', Set.of('A', 'B', 'C'))
            .put('3', Set.of('D', 'E', 'F'))
            .put('4', Set.of('G', 'H', 'I'))
            .put('5', Set.of('J', 'K', 'L'))
            .put('6', Set.of('M', 'N', 'O'))
            .put('7', Set.of('P', 'Q', 'R', 'S'))
            .put('8', Set.of('T', 'U', 'V'))
            .put('9', Set.of('W', 'X', 'Y', 'Z'))
            .build();

    public static Set<String> compute(String phoneNumber) {
        Set<String> combos = new HashSet<>();

        compute(phoneNumber, 0, "", combos);

        return combos;
    }

    private static void compute(String phoneNumber, int idx, String currentCombo, Set<String> combos) {
        if (idx >= phoneNumber.length()) {
            combos.add(currentCombo);
            return;
        }
        Character c = phoneNumber.charAt(idx);

        for (Character next : numberToChars.get(c)) {
            compute(phoneNumber, idx + 1, currentCombo + next, combos);
        }
    }
}
