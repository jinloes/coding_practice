import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * You are given an array of student objects. Each student has an integer-valued age field that is to be treated
 * as a key. Rearrange the elements of the array so that students of equal age appear together. The order in which
 * different ages appear is not important.
 */
public class PartitionRepeatedEntries {
    public static void rearrange(List<Integer> entries) {
        Map<Integer, Integer> ageToCount = new HashMap<>();

        for (Integer entry : entries) {
            ageToCount.put(entry, ageToCount.getOrDefault(entry, 0) + 1);
        }

        int idx = 0;
        for (Map.Entry<Integer, Integer> entry : ageToCount.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                entries.set(idx, entry.getKey());
                idx++;
            }
        }
    }
}
