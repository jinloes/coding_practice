import java.util.*;
import java.util.stream.Collectors;

/**
 * You are given an array of strings. Compute the k strings that appear most frequently in the array.
 */
public class KMostFrequentQueries {
    public static List<String> compute(List<String> queries, int k) {
        Map<String, Integer> counts = new HashMap<>();

        for (String query : queries) {
            counts.put(query, counts.getOrDefault(query, 0) + 1);
        }

        PriorityQueue<Map.Entry<String, Integer>> maxHeap = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));

        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            maxHeap.add(entry);

            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }

        List<Map.Entry<String, Integer>> results = new ArrayList<>(maxHeap);

        results.sort(Comparator.comparingInt(Map.Entry::getValue));

        return results.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
