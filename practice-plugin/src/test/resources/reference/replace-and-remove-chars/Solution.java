package com.jinloes.practice;

public class Solution {
    public static int replaceAndRemove(char[] chars, int size) {
        int write = 0, aCount = 0;
        for (int read = 0; read < size; read++) {
            if (chars[read] == 'b') continue;
            if (chars[read] == 'a') aCount++;
            chars[write++] = chars[read];
        }
        int finalSize = write + aCount;
        int read = write - 1, out = finalSize - 1;
        while (read >= 0) {
            if (chars[read] == 'a') { chars[out--] = 'd'; chars[out--] = 'd'; }
            else chars[out--] = chars[read];
            read--;
        }
        return finalSize;
    }
}
