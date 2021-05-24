import java.util.Comparator;
import java.util.List;

/**
 * Given service times for a set of queries, compute a schedule for processing the queries that minimizes the total
 * aiting time. Return the minimum waiting time.
 */
public class MinimizeWaitTime {
    public static int minimumTotalWaitingTime(List<Integer> serviceTimes) {
        serviceTimes.sort(Comparator.naturalOrder());

        int totalWaitingTime = 0;

        for (int i = 0; i < serviceTimes.size(); i++) {
            int remainingQueries = serviceTimes.size() - (i + 1);
            totalWaitingTime += serviceTimes.get(i) * remainingQueries;
        }

        return totalWaitingTime;
    }
}
