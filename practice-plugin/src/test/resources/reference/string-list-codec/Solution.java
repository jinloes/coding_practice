package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static String encode(List<String> strings) {
        StringBuilder encoded = new StringBuilder();
        for (String value : strings) {
            encoded.append(value.length()).append('#').append(value);
        }
        return encoded.toString();
    }

    public static List<String> decode(String encoded) {
        List<String> strings = new ArrayList<>();
        int index = 0;
        while (index < encoded.length()) {
            int marker = encoded.indexOf('#', index);
            int length = Integer.parseInt(encoded.substring(index, marker));
            strings.add(encoded.substring(marker + 1, marker + 1 + length));
            index = marker + 1 + length;
        }
        return strings;
    }
}
