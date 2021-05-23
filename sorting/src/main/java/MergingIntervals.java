import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Write a program which takes as input an array of disjoint closed intervals with integer endpoints, sorted by
 * increasing order of left endpoint, and an interval to be added, and returns the union of the intervals in the
 * array and the added interval. Your result should be expressed as a union of disjoint intervals sorted by
 * left endpoint.
 */
public class MergingIntervals {
    public static List<Tuple2<Integer, Integer>> merge(List<Tuple2<Integer, Integer>> intervals) {
        intervals.sort(Comparator.comparing(Tuple2::_1));

        List<Tuple2<Integer, Integer>> result = new ArrayList<>();
        result.add(intervals.get(0));

        int writeIdx = 1;

        for (int i = 1; i < intervals.size(); i++) {
            Tuple2<Integer, Integer> interval = intervals.get(i);
            Tuple2<Integer, Integer> previous = result.get(writeIdx - 1);

            if (intersects(interval, previous)) {
                Tuple2<Integer, Integer> newTuple = Tuple.of(previous._1, Math.max(previous._2(), interval._2()));
                result.set(writeIdx - 1, newTuple);
            } else {
                result.add(interval);
                writeIdx++;
            }
        }
        return result;
    }

    private static boolean intersects(Tuple2<Integer, Integer> i1, Tuple2<Integer, Integer> i2) {
        return i2._1() < i1._2() && i2._2() > i1._1();
    }
}
