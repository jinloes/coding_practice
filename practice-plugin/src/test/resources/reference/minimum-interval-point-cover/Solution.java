package com.jinloes.practice;

import java.util.*;

public class Solution {
    public record Interval(int start, int end) {}
    public static int[] minimumCoverPoints(Interval[] intervals) {
        Interval[] sorted = intervals.clone();
        Arrays.sort(sorted, Comparator.comparingInt(Interval::end).thenComparingInt(Interval::start));
        ArrayList<Integer> points = new ArrayList<>();
        Integer chosen = null;
        for (Interval in : sorted) if (chosen == null || in.start() > chosen) { chosen = in.end(); points.add(chosen); }
        return points.stream().mapToInt(Integer::intValue).toArray();
    }
}
