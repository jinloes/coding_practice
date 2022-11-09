import java.util.HashSet;
import java.util.Set;

/**
 * Write an efficient function that deletes characters from a mutable ASCII string.
 */
public class DeleteChars {
    public static String delete(char[] str, String toRemove) {
        Set<Character> removeSet = new HashSet<>();
        for (char c : toRemove.toCharArray()) {
            removeSet.add(c);
        }

        StringBuilder builder = new StringBuilder();

        for (char c : str) {
            if (!removeSet.contains(c)) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    public static String deleteWithoutExtraMemory(char[] str, String toRemove) {
        Set<Character> removeSet = new HashSet<>();
        for (char c : toRemove.toCharArray()) {
            removeSet.add(c);
        }

        int readIdx = 0;
        int writeIdx = 0;

        while (readIdx < str.length) {
            if (!removeSet.contains(str[readIdx])) {
                str[writeIdx] = str[readIdx];
                writeIdx++;
            }
            readIdx++;
        }

        return new String(str, 0, writeIdx);
    }
}
