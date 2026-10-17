package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> splitIpAddresses(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.length() < 4 || digits.length() > 12) {
            return result;
        }
        search(digits, 0, new ArrayList<>(), result);
        return result;
    }

    private static void search(String digits, int index, List<String> parts, List<String> result) {
        if (parts.size() == 4) {
            if (index == digits.length()) {
                result.add(String.join(".", parts));
            }
            return;
        }
        int remainingParts = 4 - parts.size();
        int remainingDigits = digits.length() - index;
        if (remainingDigits < remainingParts || remainingDigits > remainingParts * 3) {
            return;
        }
        for (int length = 1; length <= 3 && index + length <= digits.length(); length++) {
            String part = digits.substring(index, index + length);
            if (isValid(part)) {
                parts.add(part);
                search(digits, index + length, parts, result);
                parts.remove(parts.size() - 1);
            }
        }
    }

    private static boolean isValid(String part) {
        if (part.length() > 1 && part.charAt(0) == '0') {
            return false;
        }
        return Integer.parseInt(part) <= 255;
    }
}
