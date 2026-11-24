package com.jinloes.practice;

public final class Solution {
    private Solution() {
    }

    public static int bestClosingHour(String log) {
        int delta = 0;
        int bestDelta = 0;
        int bestHour = 0;
        for (int i = 0; i < log.length(); i++) {
            delta += (log.charAt(i) == 'Y') ? -1 : 1;
            if (delta < bestDelta) {
                bestDelta = delta;
                bestHour = i + 1;
            }
        }
        return bestHour;
    }
}
