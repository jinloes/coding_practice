package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class Solution {
    private Solution() {
    }

    public static List<String> matchLanguages(String header, List<String> supported) {
        if (header.isEmpty() || supported.isEmpty()) {
            return List.of();
        }
        Map<String, String> exact = new HashMap<>(supported.size() * 2);
        Map<String, List<String>> byBase = new HashMap<>();
        for (String tag : supported) {
            String lower = tag.toLowerCase(Locale.ROOT);
            exact.put(lower, tag);
            int dash = lower.indexOf('-');
            String base = dash >= 0 ? lower.substring(0, dash) : lower;
            byBase.computeIfAbsent(base, k -> new ArrayList<>()).add(tag);
        }

        List<String> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        int start = 0;
        int len = header.length();
        while (start <= len) {
            int comma = header.indexOf(',', start);
            int end = comma >= 0 ? comma : len;
            String token = header.substring(start, end).trim();
            if (!token.isEmpty()) {
                if ("*".equals(token)) {
                    for (String tag : supported) {
                        if (seen.add(tag)) {
                            result.add(tag);
                        }
                    }
                    break;
                }
                String lower = token.toLowerCase(Locale.ROOT);
                if (lower.indexOf('-') >= 0) {
                    String matched = exact.get(lower);
                    if (matched != null && seen.add(matched)) {
                        result.add(matched);
                    }
                } else {
                    List<String> group = byBase.get(lower);
                    if (group != null) {
                        for (String tag : group) {
                            if (seen.add(tag)) {
                                result.add(tag);
                            }
                        }
                    }
                }
            }
            if (comma < 0) {
                break;
            }
            start = comma + 1;
        }
        return result;
    }
}
