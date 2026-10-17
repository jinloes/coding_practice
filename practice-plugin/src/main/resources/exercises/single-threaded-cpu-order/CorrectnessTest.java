package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void returnsEmptyOrderForNoTasks() { assertThat(Solution.processingOrder(new int[0][2])).isEmpty(); }
    @Test
    void jumpsAcrossIdleTime() { assertThat(Solution.processingOrder(new int[][]{{10,3},{20,1},{20,2}})).containsExactly(0,1,2); }
    @Test
    void doesNotMutateInput() { int[][] tasks = {{3,9},{1,2},{2,1}}; int[][] before = Arrays.stream(tasks).map(int[]::clone).toArray(int[][]::new); Solution.processingOrder(tasks); assertThat(Arrays.deepToString(tasks)).isEqualTo(Arrays.deepToString(before)); }
    @Test
    void handlesManyEqualProcessingTimes() { assertThat(Solution.processingOrder(new int[][]{{0,5},{0,5},{1,1},{1,1}})).containsExactly(0,2,3,1); }
    @Test
    void matchesReferenceOnSeededWorkload() { int[][] tasks = new int[80][2]; for (int i = 0; i < tasks.length; i++) { tasks[i][0] = (i * 17) % 31; tasks[i][1] = 1 + (i * 19) % 11; } assertThat(Solution.processingOrder(tasks)).containsExactly(expected(tasks)); }
    private static int[] expected(int[][] tasks) { int n = tasks.length; int[][] indexed = new int[n][3]; for (int i = 0; i < n; i++) indexed[i] = new int[]{tasks[i][0], tasks[i][1], i}; Arrays.sort(indexed, (a,b) -> a[0] != b[0] ? Integer.compare(a[0], b[0]) : Integer.compare(a[2], b[2])); PriorityQueue<int[]> ready = new PriorityQueue<>((a,b) -> a[1] != b[1] ? Integer.compare(a[1], b[1]) : Integer.compare(a[2], b[2])); int[] order = new int[n]; long time = 0; int next = 0; for (int out = 0; out < n; out++) { if (ready.isEmpty() && time < indexed[next][0]) time = indexed[next][0]; while (next < n && indexed[next][0] <= time) ready.add(indexed[next++]); int[] task = ready.remove(); order[out] = task[2]; time += task[1]; } return order; }
}
