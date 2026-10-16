package com.jinloes.practice;

import java.util.Collections;
import java.util.PriorityQueue;

public class Solution {
    public static int smash(int[] stones) {
        PriorityQueue<Integer> heaviest = new PriorityQueue<>(Collections.reverseOrder());
        for (int stone : stones) {
            heaviest.add(stone);
        }
        while (heaviest.size() > 1) {
            int first = heaviest.poll();
            int second = heaviest.poll();
            if (first != second) {
                heaviest.add(first - second);
            }
        }
        return heaviest.peek();
    }
}
