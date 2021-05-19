import java.util.HashMap;
import java.util.Map;

/**
 * Given 2 strings return if they are one or zero edits away. Insert, remove, replace
 */
public class OneEdit {
    public boolean isOneEditAway(String s1, String s2) {
        String toLook = s1;
        String search = s2;
        if (s1.length() < s2.length()) {
            toLook = s2;
            search = s1;
        }

        Map<Character, Integer> toLookCount = new HashMap<>();

        for (int i = 0; i < toLook.length(); i++) {
            toLookCount.compute(toLook.charAt(i), ((character, integer) -> integer == null ? 1 : integer + 1));
        }

        for (int i = 0; i < search.length(); i++) {
            toLookCount.compute(search.charAt(i), ((character, integer) -> {
                if (integer == null) {
                    return null;
                }
                if (integer - 1 == 0) {
                    return null;
                }
                return integer - 1;
            }));
        }
        return toLookCount.size() <= 1;
    }
}
