package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static int minRooms(int[][] meetings) {
        int count = meetings.length;
        int[] starts = new int[count];
        int[] ends = new int[count];
        for (int i = 0; i < count; i++) {
            starts[i] = meetings[i][0];
            ends[i] = meetings[i][1];
        }
        Arrays.sort(starts);
        Arrays.sort(ends);
        int rooms = 0;
        int best = 0;
        int endIndex = 0;
        for (int start : starts) {
            while (ends[endIndex] <= start) {
                endIndex++;
                rooms--;
            }
            rooms++;
            best = Math.max(best, rooms);
        }
        return best;
    }
}
