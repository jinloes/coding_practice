package com.jinloes.practice;

public class Solution {
    public static int minSumOfLengths(int[] numbers, int target) {
        int found = 0, total = 0, sum = 0, start = 0;
        for (int end = 0; end < numbers.length; end++) {
            sum += numbers[end];
            while (sum > target && start <= end) sum -= numbers[start++];
            if (sum == target) { found++; total += end - start + 1; sum = 0; start = end + 1; }
        }
        return found >= 2 ? total : -1;
    }
}
