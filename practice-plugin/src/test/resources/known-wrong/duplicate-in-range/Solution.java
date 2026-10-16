package com.jinloes.practice;

public class Solution {
    public static int findDuplicate(int[] nums) {
        int n = nums.length - 1;
        int sum = 0;
        for (int value : nums) {
            sum += value;
        }
        return sum - n * (n + 1) / 2;
    }
}
