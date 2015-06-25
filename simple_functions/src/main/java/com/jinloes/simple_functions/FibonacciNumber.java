package com.jinloes.simple_functions;

/**
 * Calculates a fibionacci number.
 * The algorithm is Fn = Fn-1 + Fn-2.
 */
public class FibonacciNumber {
    public static int getNumber(int n) {
        int previousPrevious = 0;
        int previous = 1;
        if(n == 1) {
            return previousPrevious;
        }
        if(n== 2){
            return previous;
        }
        int num = 0;
        for(int i = 3; i<=n; i++) {
            num = previous + previousPrevious;
            previousPrevious = previous;
            previous = num;
        }
        return num;
    }
}
