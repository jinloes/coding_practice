package com.jinloes.practice;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
public class Solution {
    public static boolean canFinish(int courseCount, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < courseCount; i++) graph.add(new ArrayList<>());
        int[] incoming = new int[courseCount];
        for (int[] pair : prerequisites) {
            graph.get(pair[1]).add(pair[0]);
            incoming[pair[0]]++;
        }
        ArrayDeque<Integer> ready = new ArrayDeque<>();
        for (int i = 0; i < courseCount; i++) if (incoming[i] == 0) ready.add(i);
        int completed = 0;
        while (!ready.isEmpty()) {
            int course = ready.remove();
            completed++;
            for (int next : graph.get(course)) if (--incoming[next] == 0) ready.add(next);
        }
        return completed == courseCount;
    }
}
