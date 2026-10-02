package com.jinloes.practice;

import java.util.Arrays;
import java.util.List;

public class Solution {
    public static String encode(List<String> strings) {
        return String.join(",", strings);
    }

    public static List<String> decode(String encoded) {
        return Arrays.asList(encoded.split(","));
    }
}
