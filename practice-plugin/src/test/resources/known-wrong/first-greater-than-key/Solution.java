package com.jinloes.practice;

public class Solution {
    public static int firstGreaterThan(int[] numbers, int key) {
        int lo=0, hi=numbers.length;
        while(lo<hi){ int mid=lo+(hi-lo)/2; if(numbers[mid]<key) lo=mid+1; else hi=mid; }
        return lo==numbers.length ? -1 : lo;
    }
}
