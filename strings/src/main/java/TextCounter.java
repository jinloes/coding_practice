import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Takes in a paragraph as a string and returns a report of count of the words sorted by word length and then the words
 * lexicographically.
 * <p>
 * ie ab ab -> 2 ab
 */
public class TextCounter {
    private static final Pattern NON_WORD_CHAR = Pattern.compile("[^a-zA-Z1-9]$");

    public List<String> count(String text) {
        if (text == null || text.isEmpty()) {
            return new ArrayList<>();
        }

        StringTokenizer tokenizer = new StringTokenizer(text);

        Comparator<String> textCounterComparator = (o1, o2) -> {
            if (o1.length() == o2.length()) {
                return o1.compareTo(o2);
            } else {
                return o1.length() - o2.length();
            }
        };

        Map<String, Integer> countMap = new TreeMap<>(textCounterComparator);

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            token = NON_WORD_CHAR.matcher(token).replaceAll("");
            System.out.println(token);
            if (countMap.containsKey(token)) {
                int count = countMap.get(token);
                countMap.put(token, count + 1);
            } else {
                countMap.put(token, 1);
            }
        }

        System.out.println(countMap);

        return countMap.entrySet()
                .stream()
                .map(entry -> entry.getValue() + " " + entry.getKey())
                .collect(Collectors.toList());
    }
}
