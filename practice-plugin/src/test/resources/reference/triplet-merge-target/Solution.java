package com.jinloes.practice;

public class Solution {
    public static boolean canMerge(int[][] triplets, int[] target) {
        boolean[] hit = new boolean[3];
        for (int[] triplet : triplets) {
            if (triplet[0] > target[0] || triplet[1] > target[1] || triplet[2] > target[2]) {
                continue;
            }
            for (int k = 0; k < 3; k++) {
                if (triplet[k] == target[k]) {
                    hit[k] = true;
                }
            }
        }
        return hit[0] && hit[1] && hit[2];
    }
}
