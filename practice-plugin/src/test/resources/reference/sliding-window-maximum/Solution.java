package com.jinloes.practice;
import java.util.ArrayDeque;
public class Solution {
    public static int[] maxSlidingWindow(int[] numbers, int windowSize) {
        int[] result = new int[numbers.length - windowSize + 1];
        ArrayDeque<Integer> candidates = new ArrayDeque<>();
        for (int i = 0; i < numbers.length; i++) {
            while (!candidates.isEmpty() && candidates.peekFirst() <= i - windowSize) candidates.removeFirst();
            while (!candidates.isEmpty() && numbers[candidates.peekLast()] <= numbers[i]) candidates.removeLast();
            candidates.addLast(i);
            if (i >= windowSize - 1) result[i - windowSize + 1] = numbers[candidates.peekFirst()];
        }
        return result;
    }
}
