package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class Solution {
    private Solution() {
    }

    public static List<String> matchLanguages(String header, List<String> supported) {
        List<String> result = new ArrayList<>();
        // Bug: uses startsWith without checking '-' boundary and does not deduplicate.
        for (String raw : header.split(",")) {
            String token = raw.trim().toLowerCase(Locale.ROOT);
            if (token.isEmpty()) {
                continue;
            }
            for (String tag : supported) {
                if ("*".equals(token) || tag.toLowerCase(Locale.ROOT).startsWith(token)) {
                    result.add(tag);
                }
            }
        }
        return result;
    }
}
