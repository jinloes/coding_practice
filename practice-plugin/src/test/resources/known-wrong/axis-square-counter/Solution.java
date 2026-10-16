package com.jinloes.practice;

import java.util.HashSet;
import java.util.Set;

public class Solution {
    private static final int WIDTH = 1001;

    private final Set<Integer> points = new HashSet<>();

    public void add(int x, int y) {
        points.add(x * WIDTH + y);
    }

    public int count(int x, int y) {
        int total = 0;
        for (int key : points) {
            int px = key / WIDTH;
            int py = key % WIDTH;
            int dx = px - x;
            int dy = py - y;
            if (dx == 0 || Math.abs(dx) != Math.abs(dy)) {
                continue;
            }
            if (points.contains(px * WIDTH + y) && points.contains(x * WIDTH + py)) {
                total++;
            }
        }
        return total;
    }
}
