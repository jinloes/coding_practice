package com.jinloes.practice;

public class Solution {
    public static int[] targetRange(int[] numbers, int target) {
        int first = firstGreaterEqual(numbers, target);
        if (first == numbers.length || numbers[first] != target) return new int[]{-1, -1};
        return new int[]{first, firstGreater(numbers, target) - 1};
    }
    private static int firstGreaterEqual(int[] a, int target) { int lo=0, hi=a.length; while(lo<hi){ int mid=lo+(hi-lo)/2; if(a[mid]<target) lo=mid+1; else hi=mid; } return lo; }
    private static int firstGreater(int[] a, int target) { int lo=0, hi=a.length; while(lo<hi){ int mid=lo+(hi-lo)/2; if(a[mid]<=target) lo=mid+1; else hi=mid; } return lo; }
}
