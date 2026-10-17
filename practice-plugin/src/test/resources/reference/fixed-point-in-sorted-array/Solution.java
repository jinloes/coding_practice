package com.jinloes.practice;

public class Solution {
    public static int findFixedPoint(int[] numbers) {
        int lo=0, hi=numbers.length-1;
        while(lo<=hi){ int mid=lo+(hi-lo)/2; if(numbers[mid]==mid) return mid; if(numbers[mid]>mid) hi=mid-1; else lo=mid+1; }
        return -1;
    }
}
