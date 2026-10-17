package com.jinloes.practice;

public class Solution {
    public static void merge(int[] first, int m, int[] second, int n) {
        int i=m-1, j=n-1, write=m+n-1;
        while(j>=0) { if(i>=0 && first[i] > second[j]) first[write--] = first[i--]; else first[write--] = second[j--]; }
    }
}
