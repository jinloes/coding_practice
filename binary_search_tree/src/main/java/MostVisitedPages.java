import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.*;

/**
 * Write a function to read the next line from a log file, and a function to find the k most visited pages, where k
 * is an input to the function. Optimize performance for the situation where calls to the two functions are
 * interleaved.
 * <p>
 * You can assume the set of distinct pages is small enough to fit in RAM.
 */
public class MostVisitedPages {
    public static List<String> get(List<String> pagesVisited, int k) {
        Map<String, Integer> countMap = new HashMap<>();
        TreeSet<Tuple2<String, Integer>> pageCounts = new TreeSet<>(
                (o1, o2) -> {
                    int compare = o2._2() - o1._2();

                    if (compare == 0) {
                        return o1._1().compareTo(o2._1());
                    }
                    return compare;
                });

        // O(n log n)
        for (String page : pagesVisited) {
            Integer oldValue = countMap.put(page, countMap.getOrDefault(page, 0) + 1);
            oldValue = oldValue == null ? 0 : oldValue;
            pageCounts.remove(Tuple.of(page, oldValue));
            pageCounts.add(Tuple.of(page, oldValue + 1));
        }


        Iterator<Tuple2<String, Integer>> keys = pageCounts.iterator();

        // O(k)
        List<String> result = new LinkedList<>();
        for (int i = 0; i < k; i++) {
            result.add(keys.next()._1());
        }

        return result;
    }
}
