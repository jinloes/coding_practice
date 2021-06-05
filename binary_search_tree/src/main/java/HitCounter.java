import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Design a hit counter which counts the number of hits received in the past 5 minutes (i.e., the past 300 seconds).
 * <p>
 * Your system should accept a timestamp parameter (in seconds granularity), and you may assume that calls are being
 * made to the system in chronological order (i.e., timestamp is monotonically increasing).
 * Several hits may arrive roughly at the same time.
 * <p>
 * Implement the HitCounter class:
 * <p>
 * <p>
 * HitCounter() Initializes the object of the hit counter system.
 * void hit(int timestamp) Records a hit that happened at timestamp (in seconds). Several hits may happen at the
 * same timestamp.
 * <p>
 * int getHits(int timestamp) Returns the number of hits in the past 5 minutes from timestamp (i.e., the past 300 seconds).
 */
class HitCounter {
    private static final int SECONDS = 300;
    private TreeMap<Integer, Integer> countMap;

    /**
     * Initialize your data structure here.
     */
    public HitCounter() {
        countMap = new TreeMap<>();
    }

    /**
     * Record a hit.
     *
     * @param timestamp - The current timestamp (in seconds granularity).
     */
    public void hit(int timestamp) {
        countMap.put(timestamp, countMap.getOrDefault(timestamp, 0) + 1);
    }

    /**
     * Return the number of hits in the past 5 minutes.
     *
     * @param timestamp - The current timestamp (in seconds granularity).
     */
    public int getHits(int timestamp) {
        NavigableMap<Integer, Integer> counts = countMap.subMap(timestamp - SECONDS, false, timestamp, true);

        return counts.values()
                .stream()
                .mapToInt(val -> val)
                .sum();
    }
}
