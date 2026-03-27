import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Given a characters array tasks, representing the tasks a CPU needs to do, where each letter represents a different
 * task.
 * Tasks could be done in any order. Each task is done in one unit of time. For each unit of time, the CPU could
 * complete either one task or just be idle.
 * <p>
 * However, there is a non-negative integer n that represents the cooldown period between two same tasks
 * (the same letter in the array), that is that there must be at least n units of time between any two same tasks.
 * <p>
 * Return the least number of units of times that the CPU will take to finish all the given tasks.
 */
class TaskScheduler {
    public int leastInterval(char[] tasks, int n) {
        Map<Character, Integer> countMap = new HashMap<>();
        PriorityQueue<Count> queue = new PriorityQueue<>(Comparator.reverseOrder());

        for (char c : tasks) {
            countMap.put(c, countMap.getOrDefault(c, 0) + 1);
        }


        for (Map.Entry<Character, Integer> entry : countMap.entrySet()) {
            queue.add(new Count(entry.getKey(), entry.getValue()));
        }

        int maxCount = queue.poll().count;
        int maxTime = (maxCount - 1) * n;

        while (!queue.isEmpty()) {
            Count current = queue.poll();
            maxTime -= Math.min(maxCount - 1, current.count);
        }

        maxTime = Math.max(0, maxTime);
        return tasks.length + maxTime;
    }

    public static class Count implements Comparable<Count> {
        char c;
        int count;

        public Count(char c, int count) {
            this.c = c;
            this.count = count;
        }

        public int compareTo(Count other) {
            return count - other.count;
        }
    }
}
