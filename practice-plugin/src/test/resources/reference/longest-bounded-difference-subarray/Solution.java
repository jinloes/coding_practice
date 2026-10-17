package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    public static int longestSubarray(int[] numbers, int limit) {
        Deque<Integer> min = new ArrayDeque<>();
        Deque<Integer> max = new ArrayDeque<>();
        int left = 0, best = 0;
        for (int right = 0; right < numbers.length; right++) {
            while (!min.isEmpty() && numbers[min.peekLast()] > numbers[right]) min.removeLast();
            min.addLast(right);
            while (!max.isEmpty() && numbers[max.peekLast()] < numbers[right]) max.removeLast();
            max.addLast(right);
            while (!min.isEmpty() && !max.isEmpty()
                    && (long) numbers[max.peekFirst()] - numbers[min.peekFirst()] > limit) {
                if (min.peekFirst() == left) min.removeFirst();
                if (max.peekFirst() == left) max.removeFirst();
                left++;
            }
            best = Math.max(best, right - left + 1);
        }
        return best;
    }
}
