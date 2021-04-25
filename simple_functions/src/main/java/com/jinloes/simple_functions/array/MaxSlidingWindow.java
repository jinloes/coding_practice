package com.jinloes.simple_functions.array;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * You are given an array of integers nums, there is a sliding window of size k which is moving from the very left
 * of the array to the very right. You can only see the k numbers in the window. Each time the sliding window moves right by one position.
 * <p>
 * Return the max sliding window.
 */
public class MaxSlidingWindow {

    public int[] maxSlidingWindow(int[] nums, int k) {
        ArrayDeque<Integer> window = new ArrayDeque<>();

        List<Integer> maxVals = new ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            while (!window.isEmpty() && nums[window.peekLast()] < nums[i]) {
                window.removeLast();
            }

            window.addLast(i);


            if (!window.isEmpty() && window.getFirst() == i - k) {
                window.removeFirst();
            }

            if (i >= k - 1) {
                maxVals.add(window.peekFirst());
            }


        }

        int[] maxArr = new int[maxVals.size()];

        for (int i = 0; i < maxVals.size(); i++) {
            maxArr[i] = nums[maxVals.get(i)];
        }
        return maxArr;
    }
}