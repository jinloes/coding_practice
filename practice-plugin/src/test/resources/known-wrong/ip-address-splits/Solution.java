package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> splitIpAddresses(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null) {
            return result;
        }
        for (int a = 1; a <= 3; a++) {
            for (int b = 1; b <= 3; b++) {
                for (int c = 1; c <= 3; c++) {
                    int d = digits.length() - a - b - c;
                    if (d >= 1 && d <= 3) {
                        String one = digits.substring(0, a);
                        String two = digits.substring(a, a + b);
                        String three = digits.substring(a + b, a + b + c);
                        String four = digits.substring(a + b + c);
                        if (ok(one) && ok(two) && ok(three) && ok(four)) {
                            result.add(one + "." + two + "." + three + "." + four);
                        }
                    }
                }
            }
        }
        return result;
    }

    private static boolean ok(String part) {
        return Integer.parseInt(part) <= 255;
    }
}
