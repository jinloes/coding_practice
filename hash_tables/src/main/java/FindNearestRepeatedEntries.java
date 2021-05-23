import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Write a program which takes as input an array and finds the distance between a closest pair of equal entries.
 */
public class FindNearestRepeatedEntries {

    public static int find(List<String> entries) {
        Map<String, Integer> lastIndex = new HashMap<>();
        int shortestDist = Integer.MAX_VALUE;

        for (int i = 0; i < entries.size(); i++) {
            String current = entries.get(i);

            if (lastIndex.containsKey(current)) {
                int dist = i - lastIndex.get(current);
                shortestDist = Math.min(dist, shortestDist);
            }
            lastIndex.put(current, i);
        }

        return shortestDist;
    }

}
