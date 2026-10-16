package com.jinloes.practice;

public class Solution {
    public static int minJumps(int[] reach) {
        int last = reach.length - 1;
        int position = 0;
        int hops = 0;
        while (position < last) {
            if (position + reach[position] >= last) {
                return hops + 1;
            }
            int best = position + 1;
            for (int next = position + 1; next <= position + reach[position]; next++) {
                if (reach[next] > reach[best]) {
                    best = next;
                }
            }
            position = best;
            hops++;
        }
        return hops;
    }
}
