import java.util.HashMap;
import java.util.Map;

/**
 * In an alien language, surprisingly they also use english lowercase letters, but possibly in a different order.
 * The order of the alphabet is some permutation of lowercase letters.
 * <p>
 * Given a sequence of words written in the alien language, and the order of the alphabet,
 * return true if and only if the given words are sorted lexicographicaly in this alien language.
 */
class AlienDictionary {
    public static boolean isAlienSorted(String[] words, String order) {
        Map<Character, Integer> charToNum = new HashMap<>();


        char[] chars = order.toCharArray();

        for (int i = 0; i < order.length(); i++) {
            charToNum.put(chars[i], i);
        }

        for (int i = 1; i < words.length; i++) {
            String current = words[i];
            String previous = words[i - 1];
            int largerSize = Math.max(current.length(), current.length());

            for (int j = 0; j < largerSize; j++) {
                if (charToNum.get(current.charAt(j)) < charToNum.get(previous.charAt(j))) {
                    return false;
                }

                if (charToNum.get(current.charAt(j)) > charToNum.get(previous.charAt(j))) {
                    break;
                }

                if (j == current.length() - 1 && j != previous.length() - 1) {
                    return false;
                }

                if (j != current.length() - 1 && j == previous.length() - 1) {
                    break;
                }

            }

        }

        return true;
    }
}
