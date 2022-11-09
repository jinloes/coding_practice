import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Write an efficient function to find the first nonrepeated character in a string.
 */
public class FirstNonRepeatedChar {
    public static Character find(String str) {
        LinkedHashMap<Character, Integer> countMap = new LinkedHashMap<>();

        for (char c : str.toCharArray()) {
            countMap.putIfAbsent(c, 0);
            countMap.compute(c, (key, count) -> count + 1);
        }

        return countMap.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == 1)
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
