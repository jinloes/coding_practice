package com.jinloes.practice;

public class Solution {
    public static void merge(int[] first, int m, int[] second, int n) {
        for (int i = 0; i < n; i++) first[m + i] = second[i];
    }
}
