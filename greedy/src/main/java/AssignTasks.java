import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Design an algorithm that takes as input a set of tasks and returns an optimum assignment.
 * <p>
 * Example: 5, 2, 1, 6, 4, 4
 * Worker 1,6
 * Worker 2,5
 * Worker 4,4
 */
public class AssignTasks {
    public static List<List<Integer>> optimumTaskAssignment(List<Integer> taskDurations) {
        taskDurations.sort(Comparator.naturalOrder());

        List<List<Integer>> results = new ArrayList<>();

        for (int i = 0; i < taskDurations.size() / 2; i++) {
            results.add(List.of(taskDurations.get(i), taskDurations.get(taskDurations.size() - i - 1)));
        }

        return results;
    }
}
