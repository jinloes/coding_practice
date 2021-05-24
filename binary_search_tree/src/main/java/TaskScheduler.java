import java.util.*;

/**
 * Given a characters array tasks, representing the tasks a CPU needs to do, where each letter represents a different
 * task. Tasks could be done in any order. Each task is done in one unit of time. For each unit of time, the CPU could complete either one task or just be idle.
 * <p>
 * However, there is a non-negative integer n that represents the cooldown period between two same tasks
 * (the same letter in the array), that is that there must be at least n units of time between any two same tasks.
 * <p>
 * Return the least number of units of times that the CPU will take to finish all the given tasks.
 */
public class TaskScheduler {
    public int leastInterval(char[] tasks, int n) {
        Map<Character, Integer> countMap = new HashMap<>();
        TreeSet<List<Integer>> counts = new TreeSet<>((o1, o2) -> {
            int compare = o2.get(1) - o1.get(1);
            if (compare == 0) {
                return o1.get(0) - o2.get(0);
            }
            return compare;
        });


        for (Character c : tasks) {
            Integer oldValue = countMap.put(c, countMap.getOrDefault(c, 0) + 1);
            oldValue = oldValue == null ? 0 : oldValue;

            counts.remove(List.of((int) c, oldValue));
            counts.add(List.of((int) c, oldValue + 1));
        }

        System.out.println(counts);

        List<Integer> maxEntry = counts.first();

        System.out.println(maxEntry);
        int maxCount = maxEntry.get(1);
        int idleTime = (maxCount - 1) * n;

        Iterator<List<Integer>> it = counts.iterator();
        // skip first entry
        it.next();
        while (it.hasNext()) {
            List<Integer> current = it.next();
            idleTime -= Math.min(maxCount - 1, current.get(1));
        }

        idleTime = Math.max(0, idleTime);

        return idleTime + tasks.length;
    }
}
