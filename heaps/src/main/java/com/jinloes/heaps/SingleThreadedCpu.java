package com.jinloes.heaps;

import java.util.*;
import java.util.function.Function;

class SingleThreadedCpu {
    public int[] getOrder(int[][] tasks) {
        int[][] taskArr = new int[tasks.length][3];
        PriorityQueue<int[]> processingQueue = new PriorityQueue<>(
                Comparator.comparing((Function<int[], Integer>) task -> task[1])
                        .thenComparing(task -> task[2])
        );

        for (int i = 0; i < tasks.length; i++) {
            taskArr[i] = new int[]{tasks[i][0], tasks[i][1], i};
        }

        Arrays.sort(taskArr, Comparator.comparing(task -> task[0]));

        int time = taskArr[0][0];
        int[] result = new int[tasks.length];
        int resultIdx = 0;
        int taskIdx = 0;

        while (!processingQueue.isEmpty() || taskIdx < tasks.length) {
            while (taskIdx < tasks.length && taskArr[taskIdx][0] <= time) {
                processingQueue.add(taskArr[taskIdx]);
                taskIdx++;
            }
            if (processingQueue.isEmpty()) {
                time = taskArr[taskIdx][0];
            } else {
                int[] current = processingQueue.poll();
                result[resultIdx] = current[2];
                resultIdx++;
                time += current[1];
            }
        }

        return result;
    }
}
