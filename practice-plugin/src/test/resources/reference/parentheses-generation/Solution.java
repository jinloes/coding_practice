package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> generate(int pairs) {
        if (pairs < 0) {
            throw new IllegalArgumentException("pairs must not be negative");
        }
        List<String> result = new ArrayList<>();
        extend(new StringBuilder(2 * pairs), 0, 0, pairs, result);
        return result;
    }

    private static void extend(StringBuilder current, int open, int close, int pairs, List<String> result) {
        if (current.length() == 2 * pairs) {
            result.add(current.toString());
            return;
        }
        if (open < pairs) {
            current.append('(');
            extend(current, open + 1, close, pairs, result);
            current.deleteCharAt(current.length() - 1);
        }
        if (close < open) {
            current.append(')');
            extend(current, open, close + 1, pairs, result);
            current.deleteCharAt(current.length() - 1);
        }
    }
}
