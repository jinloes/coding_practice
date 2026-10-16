package com.jinloes.practice;

public class Solution {
    public static boolean isInterleaving(String first, String second, String merged) {
        int n = first.length();
        int m = second.length();
        if (n + m != merged.length()) {
            return false;
        }
        char[] a = first.toCharArray();
        char[] b = second.toCharArray();
        char[] c = merged.toCharArray();
        boolean[] reachable = new boolean[m + 1];
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                if (i == 0 && j == 0) {
                    reachable[0] = true;
                    continue;
                }
                boolean viaFirst = i > 0 && reachable[j] && a[i - 1] == c[i + j - 1];
                boolean viaSecond = j > 0 && reachable[j - 1] && b[j - 1] == c[i + j - 1];
                reachable[j] = viaFirst || viaSecond;
            }
        }
        return reachable[m];
    }
}
