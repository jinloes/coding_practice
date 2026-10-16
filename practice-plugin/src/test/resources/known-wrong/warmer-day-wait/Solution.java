package com.jinloes.practice;

public class Solution {
    public static int[] daysUntilWarmer(int[] temperatures) {
        int n = temperatures.length;
        int[] result = new int[n];
        int[] stack = new int[n];
        int top = 0;
        for (int day = 0; day < n; day++) {
            while (top > 0 && temperatures[stack[top - 1]] <= temperatures[day]) {
                int waiting = stack[--top];
                result[waiting] = day - waiting;
            }
            stack[top++] = day;
        }
        return result;
    }
}
