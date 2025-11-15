package com.jinloes.heaps;

import java.util.Comparator;
import java.util.PriorityQueue;

class AssignTasks {
    public int[] assignTasks(int[] servers, int[] tasks) {
        PriorityQueue<int[]> serverQueue = new PriorityQueue<>(
                Comparator.<int[]>comparingInt(arr -> arr[0])
                        .thenComparing(arr -> arr[1])
        );

        // weight, idx
        for (int i = 0; i < servers.length; i++) {
            serverQueue.add(new int[]{servers[i], i, 0});
        }

        // free time, idx
        PriorityQueue<int[]> processingQueue = new PriorityQueue<>(
                Comparator.<int[]>comparingInt(arr -> arr[2])
                        .thenComparing(arr -> arr[0])
                        .thenComparing(arr -> arr[1])
        );

        int[] result = new int[tasks.length];

        for (int time = 0; time < tasks.length; time++) {
            int taskTime = tasks[time];

            while (!processingQueue.isEmpty() && processingQueue.peek()[2] <= time) {
                serverQueue.add(processingQueue.poll());
            }

            if (serverQueue.isEmpty()) {
                int[] server = processingQueue.poll();
                result[time] = server[1];
                server[2] += taskTime;
                processingQueue.add(server);
            } else {
                int[] server = serverQueue.poll();
                result[time] = server[1];
                server[2] = time + taskTime;
                processingQueue.add(server);
            }
        }

        return result;
    }
}
