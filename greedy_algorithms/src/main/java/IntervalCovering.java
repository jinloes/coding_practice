import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * You are given a set of closed intervals. Design an efficient algorithm for finding a minimum sized set of numbers
 * that covers all the intervals.
 */
public class IntervalCovering {
    public static List<Integer> findMinimumVisits(List<Tuple2<Integer, Integer>> intervals) {
        intervals.sort(Comparator.comparing(Tuple2::_1));

        List<Integer> result = new ArrayList<>();

        int lastVisit = intervals.get(0)._2();
        result.add(lastVisit);

        for (Tuple2<Integer, Integer> interval : intervals) {
            // it the start is greater than our last visit time we have a new last visit time
            if (lastVisit < interval._1()) {
                lastVisit = interval._2();
                result.add(lastVisit);
            }
        }

        return result;
    }
}
