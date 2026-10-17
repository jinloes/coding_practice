package com.jinloes.practice;

public class Solution {
    public static int insertPosition(int[] numbers, int target) {
        int lo=0, hi=numbers.length;
        while(lo<hi){ int mid=lo+(hi-lo)/2; if(numbers[mid]<target) lo=mid+1; else hi=mid; }
        return lo;
    }
}
