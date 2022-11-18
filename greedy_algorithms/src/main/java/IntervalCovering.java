import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * You are given a set of closed intervals. Design an efficient algorithm for finding a minimum sized set of numbers
 * that covers all the intervals.
 */
public class IntervalCovering {
    public record Interval(int start, int end) {
    }

    public static List<Integer> findMinimumVisits(List<Interval> intervals) {
        intervals.sort(Comparator.comparingInt(o -> o.start));

        List<Integer> result = new ArrayList<>();
        int lastVisited = intervals.get(0).end;

        for (int i = 1; i < intervals.size(); i++) {
            Interval next = intervals.get(i);
            if (next.start > lastVisited) {
                result.add(lastVisited);
                lastVisited = next.end;
            }
        }

        result.add(lastVisited);

        return result;
    }
}
