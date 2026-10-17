package com.jinloes.practice;

public class Solution {
    public static String removeSelected(String text, String remove) {
        boolean[] removed = new boolean[Character.MAX_VALUE + 1];
        for (int i = 0; i < remove.length(); i++) {
            removed[remove.charAt(i)] = true;
        }
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (!removed[character]) {
                result.append(character);
            }
        }
        return result.toString();
    }
}
