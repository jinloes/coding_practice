package com.jinloes.practice;

public class Solution {
    public static String removeSelected(String text, String remove) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (remove.isEmpty() || remove.charAt(0) != character) {
                result.append(character);
            }
        }
        return result.toString();
    }
}
