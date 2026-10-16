package com.jinloes.practice;

public class Solution {
    public static int findUnpaired(int[] nums) {
        int unpaired = 0;
        for (int value : nums) {
            unpaired ^= value;
        }
        return unpaired;
    }
}
