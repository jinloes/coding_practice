package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final int baseLimit;
    private final long windowSeconds;
    private final int maxCredits;
    private final Map<String, long[]> states = new HashMap<>();

    public Solution(int baseLimit, int windowSeconds, int maxCredits) {
        this.baseLimit = baseLimit;
        this.windowSeconds = windowSeconds;
        this.maxCredits = maxCredits;
    }

    public boolean allow(String clientId, long timestampSeconds) {
        long window = timestampSeconds / windowSeconds;
        long[] state = states.computeIfAbsent(clientId, ignored -> new long[]{window, baseLimit + maxCredits});
        if (window > state[0]) {
            // Bug: forgets to subtract 1 for the previous window and ignores actual unused base slots.
            state[1] = baseLimit + Math.min(maxCredits, (window - state[0]) * baseLimit);
            state[0] = window;
        }
        if (state[1] <= 0) {
            return false;
        }
        state[1]--;
        return true;
    }
}
