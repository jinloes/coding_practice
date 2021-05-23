import java.util.HashMap;
import java.util.Map;

/**
 * Write a program which takes text for an anonymous letter and text for a magazine and determines if it is possible
 * to write the anonymous letter using the magazine. The anonymous letter can be written using the magazine if for
 * each character in the anonymous letter, the number of times it appears in the anonymous letter is no more than the
 * number of times it appears in the magazine.
 */
public class ConstructAnonymousLetter {
    public static boolean isLetterConstructibleFromMagazine(String letterText, String magazineText) {
        Map<Character, Integer> charCounts = new HashMap<>();

        for (char c : magazineText.toCharArray()) {
            charCounts.put(c, charCounts.getOrDefault(c, 0) + 1);
        }

        for (char c : letterText.toCharArray()) {
            if (!charCounts.containsKey(c) || charCounts.get(c) <= 0) {
                return false;
            } else {
                charCounts.put(c, charCounts.get(c) - 1);
            }
        }

        return true;
    }
}
