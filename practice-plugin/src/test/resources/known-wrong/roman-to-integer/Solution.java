package com.jinloes.practice;
public class Solution {
    public static int romanToInt(String numeral) {
        int total = 0;
        for (char symbol : numeral.toCharArray()) {
            total += switch (symbol) {
                case 'I' -> 1;
                case 'V' -> 5;
                case 'X' -> 10;
                case 'L' -> 50;
                case 'C' -> 100;
                case 'D' -> 500;
                default -> 1000;
            };
        }
        return total;
    }
}
