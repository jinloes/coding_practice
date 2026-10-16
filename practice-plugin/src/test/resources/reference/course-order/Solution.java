package com.jinloes.practice;

public class Solution {
    public static int[] courseOrder(int courses, int[][] prerequisites) {
        int[] firstEdge = new int[courses];
        java.util.Arrays.fill(firstEdge, -1);
        int[] nextEdge = new int[prerequisites.length];
        int[] blockers = new int[courses];
        for (int i = 0; i < prerequisites.length; i++) {
            int course = prerequisites[i][0];
            int before = prerequisites[i][1];
            nextEdge[i] = firstEdge[before];
            firstEdge[before] = i;
            blockers[course]++;
        }
        int[] order = new int[courses];
        int filled = 0;
        for (int course = 0; course < courses; course++) {
            if (blockers[course] == 0) {
                order[filled++] = course;
            }
        }
        for (int head = 0; head < filled; head++) {
            for (int e = firstEdge[order[head]]; e != -1; e = nextEdge[e]) {
                int course = prerequisites[e][0];
                if (--blockers[course] == 0) {
                    order[filled++] = course;
                }
            }
        }
        return filled == courses ? order : new int[0];
    }
}
