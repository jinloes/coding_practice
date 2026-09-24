package com.jinloes.practice;

public class Solution {
    public static int romanToInt(String numeral) {
        int total = 0;
        for (int index = 0; index < numeral.length(); index++) {
            int value = value(numeral.charAt(index));
            if (index + 1 < numeral.length() && value < value(numeral.charAt(index + 1))) {
                total -= value;
            } else {
                total += value;
            }
        }
        return total;
    }

    private static int value(char symbol) {
        return switch (symbol) {
            case 'I' -> 1;
            case 'V' -> 5;
            case 'X' -> 10;
            case 'L' -> 50;
            case 'C' -> 100;
            case 'D' -> 500;
            case 'M' -> 1000;
            default -> throw new IllegalArgumentException("Not a Roman symbol: " + symbol);
        };
    }
}
