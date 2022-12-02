package com.jinloes.simple_functions;

/**
 * A generic microwave supports cooking times for:
 * <p>
 * at least 1 second.
 * at most 99 minutes and 99 seconds.
 * <p>
 * To set the cooking time, you push at most four digits. The microwave normalizes what you push as four digits by
 * prepending zeroes. It interprets the first two digits as the minutes and the last two digits as the seconds.
 * It then adds them up as the cooking time. For example,
 * <p>
 * You push 9 5 4 (three digits). It is normalized as 0954 and interpreted as 9 minutes and 54 seconds.
 * You push 0 0 0 8 (four digits). It is interpreted as 0 minutes and 8 seconds.
 * You push 8 0 9 0. It is interpreted as 80 minutes and 90 seconds.
 * You push 8 1 3 0. It is interpreted as 81 minutes and 30 seconds.
 * <p>
 * You are given integers startAt, moveCost, pushCost, and targetSeconds. Initially, your finger is on the digit
 * startAt. Moving the finger above any specific digit costs moveCost units of fatigue. Pushing the digit below the
 * finger once costs pushCost units of fatigue.
 * <p>
 * There can be multiple ways to set the microwave to cook for targetSeconds seconds but you are interested in the
 * way with the minimum cost.
 * <p>
 * Return the minimum cost to set targetSeconds seconds of cooking time.
 * <p>
 * Remember that one minute consists of 60 seconds.
 */
public class MinCostCookingTimer {
    public int minCostSetTime(int startAt, int moveCost, int pushCost, int targetSeconds) {
        return helper(0, startAt, moveCost, pushCost, targetSeconds, 0, false);
    }

    public int helper(int currentDigit, int lastButton, int moveCost, int pushCost, int target, int currentSeconds, boolean buttonPressed) {
        if (currentSeconds == target && currentDigit == 4) {
            return 0;
        }
        if (currentDigit >= 4 || target < currentSeconds) {
            return Integer.MAX_VALUE;
        }

        int secs;
        if (currentDigit == 0) {
            secs = 600;
        } else if (currentDigit == 1) {
            secs = 60;
        } else if (currentDigit == 2) {
            secs = 10;
        } else {
            secs = 1;
        }
        int c1 = Integer.MAX_VALUE;
        if (!buttonPressed) {
            c1 = helper(currentDigit + 1, lastButton, moveCost, pushCost, target, currentSeconds, false);
        }
        int c2 = Integer.MAX_VALUE;
        for (int i = 0; i < 10; i++) {
            int cost = helper(currentDigit + 1, i, moveCost, pushCost, target, currentSeconds + (i * secs), true);
            if (cost == Integer.MAX_VALUE) {
                continue;
            }
            int movementFactor = 1;
            if (lastButton == i) {
                movementFactor = 0;
            }
            c2 = Math.min(c2, (movementFactor * moveCost) + pushCost + cost);
        }
        return Math.min(c1, c2);
    }
}
