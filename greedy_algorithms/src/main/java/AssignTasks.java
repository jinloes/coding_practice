import io.vavr.Tuple;
import io.vavr.Tuple2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Design an algorithm that takes as input a set of tasks and returns an optimum assignment.
 */
public class AssignTasks {
    public static List<Tuple2<Integer, Integer>> optimumTaskAssignment(List<Integer> taskDurations) {
        taskDurations.sort(Comparator.naturalOrder());

        List<Tuple2<Integer, Integer>> results = new ArrayList<>();

        for (int i = 0; i < taskDurations.size() / 2; i++) {
            results.add(Tuple.of(taskDurations.get(i), taskDurations.get(taskDurations.size() - i - 1)));
        }

        return results;
    }
}
