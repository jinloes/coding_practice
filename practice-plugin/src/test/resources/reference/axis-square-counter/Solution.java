package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private static final int WIDTH = 1001;

    private final Map<Integer, Integer> copies = new HashMap<>();

    public void add(int x, int y) {
        copies.merge(x * WIDTH + y, 1, Integer::sum);
    }

    public int count(int x, int y) {
        int total = 0;
        for (Map.Entry<Integer, Integer> entry : copies.entrySet()) {
            int px = entry.getKey() / WIDTH;
            int py = entry.getKey() % WIDTH;
            int dx = px - x;
            int dy = py - y;
            if (dx == 0 || Math.abs(dx) != Math.abs(dy)) {
                continue;
            }
            int sideOne = copies.getOrDefault(px * WIDTH + y, 0);
            int sideTwo = copies.getOrDefault(x * WIDTH + py, 0);
            total += entry.getValue() * sideOne * sideTwo;
        }
        return total;
    }
}
