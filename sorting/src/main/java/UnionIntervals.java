import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Design an algorithm that takes as input a set of intervals, and outputs their union expressed as a set of disjoint
 * intervals.
 */
public class UnionIntervals {
    public static List<Tuple2<Integer, Integer>> unionOfIntervals(List<Tuple2<Integer, Integer>> intervals) {
        intervals.sort(Comparator.comparing(Tuple2::_1));

        List<Tuple2<Integer, Integer>> result = new ArrayList<>();
        result.add(intervals.get(0));
        int writeIdx = 1;

        for (int i = 1; i < intervals.size(); i++) {
            Tuple2<Integer, Integer> current = intervals.get(i);
            Tuple2<Integer, Integer> previous = result.get(writeIdx - 1);

            if (intersects(previous, current)) {
                Tuple2<Integer, Integer> newTuple = previous.map2(integer -> Math.max(current._2(), previous._2()));
                result.set(writeIdx - 1, newTuple);
            } else {
                result.add(current);
                writeIdx++;
            }
        }

        return result;

    }

    private static boolean intersects(Tuple2<Integer, Integer> i1, Tuple2<Integer, Integer> i2) {
        return i2._2() >= i1._1() && i2._1() <= i1._2();
    }
}
