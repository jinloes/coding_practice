package com.jinloes.simple_functions;

/**
 * Calculates a fibionacci number. The algorithm is Fn = Fn-1 + Fn-2.
 */
public class FibonacciNumber {
    /**
     * Calculates the nth fibionacci number.
     */
    public static int getNumber(int fibNum) {
        int previousPrevious = 0;
        int previous = 1;
        if (fibNum == 1) {
            return previousPrevious;
        }
        if (fibNum == 2) {
            return previous;
        }
        int num = 0;
        for (int i = 3; i <= fibNum; i++) {
            num = previous + previousPrevious;
            previousPrevious = previous;
            previous = num;
        }
        return num;
    }
}
