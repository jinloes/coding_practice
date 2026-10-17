package com.jinloes.practice;

public class Solution {
    public static boolean contains(int[][] matrix, int target) {
        if (matrix.length == 0 || matrix[0].length == 0) return false;
        int rows=matrix.length, cols=matrix[0].length, lo=0, hi=rows*cols-1;
        while(lo<=hi){ int mid=lo+(hi-lo)/2; int value=matrix[mid/cols][mid%cols]; if(value==target) return true; if(value<target) lo=mid+1; else hi=mid-1; }
        return false;
    }
}
