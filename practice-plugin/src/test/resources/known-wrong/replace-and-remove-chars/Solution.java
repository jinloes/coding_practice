package com.jinloes.practice;

public class Solution {
    public static int replaceAndRemove(char[] chars, int size) {
        int write = 0;
        for (int i = 0; i < size; i++) if (chars[i] != 'b') chars[write++] = chars[i];
        return write;
    }
}
