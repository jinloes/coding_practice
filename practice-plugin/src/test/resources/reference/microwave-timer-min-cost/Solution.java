package com.jinloes.practice;

public class Solution {
    public static int minCost(int startDigit, int moveCost, int pushCost, int targetSeconds) {
        int best = Integer.MAX_VALUE;
        for (int minutes = 0; minutes <= 99; minutes++) {
            int seconds = targetSeconds - minutes * 60;
            if (seconds < 0 || seconds > 99) continue;
            best = Math.min(best, cost(Integer.toString(minutes * 100 + seconds), startDigit, moveCost, pushCost));
        }
        return best;
    }
    private static int cost(String buttons, int finger, int moveCost, int pushCost) {
        int cost = 0;
        for (int i = 0; i < buttons.length(); i++) {
            int digit = buttons.charAt(i) - '0';
            if (finger != digit) { cost += moveCost; finger = digit; }
            cost += pushCost;
        }
        return cost;
    }
}
