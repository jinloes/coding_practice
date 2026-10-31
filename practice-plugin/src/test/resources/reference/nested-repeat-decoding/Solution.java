package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    public static String decode(String encoded) {
        Deque<StringBuilder> outer = new ArrayDeque<>();
        Deque<Integer> counts = new ArrayDeque<>();
        StringBuilder current = new StringBuilder();
        int count = 0;
        for (int i = 0; i < encoded.length(); i++) {
            char ch = encoded.charAt(i);
            if (ch >= '0' && ch <= '9') {
                count = count * 10 + (ch - '0');
            } else if (ch == '[') {
                counts.push(count);
                outer.push(current);
                current = new StringBuilder();
                count = 0;
            } else if (ch == ']') {
                String inner = current.toString();
                current = outer.pop();
                current.append(inner.repeat(counts.pop()));
            } else {
                current.append(ch);
            }
        }
        return current.toString();
    }
}
