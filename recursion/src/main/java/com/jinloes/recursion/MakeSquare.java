package com.jinloes.recursion;

import java.util.Arrays;

class MakeSquare {
    public boolean makesquare(int[] matchsticks) {
        int sum = Arrays.stream(matchsticks)
                .sum();
        if (sum % 4 != 0) {
            return false;
        }

        int[] sides = new int[4];
        return dfs(matchsticks, sides, 0);
    }

    private boolean dfs(int[] matchsticks, int[] sides, int idx) {
        if (idx == matchsticks.length) {
            return sides[0] == sides[1]
                    && sides[1] == sides[2]
                    && sides[2] == sides[3];
        }

        for (int i = 0; i < 4; i++) {
            sides[i] += matchsticks[idx];
            if (dfs(matchsticks, sides, idx + 1)) {
                return true;
            }
            sides[i] -= matchsticks[idx];
        }

        return false;
    }
}
