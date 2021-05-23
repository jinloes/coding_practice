import io.vavr.Tuple2;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Write a program that takes a set of events, and determines the maximum number of events that take place concurrently.
 */
public class RenderCalendar {
    public static int findMaxSimultaneousEvents(List<Tuple2<Integer, Integer>> events) {
        events.sort(Comparator.comparing(Tuple2::_1));

        PriorityQueue<Tuple2<Integer, Integer>> minHeap = new PriorityQueue<>(Comparator.comparing(Tuple2::_2));

        int maxEvents = 0;

        for (Tuple2<Integer, Integer> event : events) {

            // remove events while start is after the ending of the min ending event
            while (!minHeap.isEmpty() && minHeap.peek()._2() < event._1()) {
                minHeap.poll();
            }

            minHeap.add(event);

            maxEvents = Math.max(maxEvents, minHeap.size());
        }

        return maxEvents;
    }
}
