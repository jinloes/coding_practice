package com.jinloes.simple_functions.dynamic_programming;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * A child is running up a staircase with n steps and can hop either 1, 2, or 3 steps at a time.
 * Count how many possible ways the child can run up the stairs.
 */
public class TripleStep {
    private Map<Integer, BigDecimal> memo = new HashMap<>();

    public BigDecimal count(int steps) {
        if (steps == 0) {
            return BigDecimal.ONE;
        } else if (steps < 0) {
            return BigDecimal.ZERO;
        }

        // run time O(3^n) without optimization
        BigDecimal threeStep = memo.get(steps - 3);
        if (threeStep == null) {
            threeStep = count(steps - 3);
            memo.put(steps - 3, threeStep);
        }

        BigDecimal twoStep = memo.get(steps - 2);
        if (twoStep == null) {
            twoStep = count(steps - 2);
            memo.put(steps - 2, twoStep);
        }

        BigDecimal oneStep = memo.get(steps - 1);
        if (oneStep == null) {
            oneStep = count(steps - 1);
            memo.put(steps - 1, oneStep);
        }
        return threeStep.add(twoStep).add(oneStep);
    }
}
