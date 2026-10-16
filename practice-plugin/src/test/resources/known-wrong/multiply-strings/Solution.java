package com.jinloes.practice;

public class Solution {
    public static String multiply(String left, String right) {
        int[] cells = new int[left.length() + right.length()];
        for (int i = left.length() - 1; i >= 0; i--) {
            int a = left.charAt(i) - '0';
            for (int j = right.length() - 1; j >= 0; j--) {
                int total = cells[i + j + 1] + a * (right.charAt(j) - '0');
                cells[i + j + 1] = total % 10;
                cells[i + j] += total / 10;
            }
        }
        StringBuilder product = new StringBuilder(cells.length);
        for (int cell : cells) {
            product.append((char) ('0' + cell));
        }
        return product.toString();
    }
}
