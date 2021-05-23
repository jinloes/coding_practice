import io.vavr.Tuple;
import io.vavr.Tuple3;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Design an algorithm that takes three sorted arrays and returns one entry from each such that the minimum interval
 * containing these three entries is as small as possible.
 */
public class FindClosestEntries {
    public static List<Integer> compute(List<List<Integer>> sortedLists) {
        TreeSet<Tuple3<Integer, Integer, Integer>> tupleSet = new TreeSet<>(
                (o1, o2) -> {
                    int compare = o1._1() - o2._1();
                    if (compare == 0) {
                        return o1._2() - o2._2();
                    }
                    return compare;
                });
        int minIntervalSize = Integer.MAX_VALUE;
        List<Integer> minInterval = new ArrayList<>();

        // O(m) total number of lists
        for (int i = 0; i < sortedLists.size(); i++) {
            // Tuple will be value, list idx, idx in list
            tupleSet.add(Tuple.of(sortedLists.get(i).get(0), i, 0));
        }


        // O(n) - size of shortest list
        while (true) {
            Tuple3<Integer, Integer, Integer> minEntry = tupleSet.first();
            Tuple3<Integer, Integer, Integer> maxEntry = tupleSet.last();
            int intervalSize = maxEntry._1() - minEntry._1();

            if (intervalSize < minIntervalSize) {
                minIntervalSize = intervalSize;
                minInterval.clear();

                // O(m) - total number of lists
                minInterval = tupleSet.stream()
                        .map(Tuple3::_1)
                        .collect(Collectors.toList());
            }


            if (minEntry._3() >= sortedLists.get(minEntry._2()).size() - 1) {
                return minInterval;
            }

            int nextIdx = minEntry._3() + 1;
            int nextVal = sortedLists.get(minEntry._2()).get(nextIdx);

            // log(m)
            tupleSet.remove(minEntry);
            // log(m)
            tupleSet.add(Tuple.of(nextVal, minEntry._2(), nextIdx));
        }

    }
}
