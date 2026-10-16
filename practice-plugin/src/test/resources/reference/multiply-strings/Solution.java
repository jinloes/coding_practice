package com.jinloes.practice;

public class Solution {
    public static String multiply(String left, String right) {
        if (left.equals("0") || right.equals("0")) {
            return "0";
        }
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
        int first = cells[0] == 0 ? 1 : 0;
        for (int i = first; i < cells.length; i++) {
            product.append((char) ('0' + cells[i]));
        }
        return product.toString();
    }
}
