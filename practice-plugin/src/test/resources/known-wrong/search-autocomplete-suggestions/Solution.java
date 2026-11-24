package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public final class Solution {
    private Solution() {
    }

    public static List<List<String>> suggest(String[] products, String query) {
        // Bug: fails to sort lexicographically and mutates/returns in arbitrary order.
        List<List<String>> result = new ArrayList<>();
        for (int i = 1; i <= query.length(); i++) {
            String prefix = query.substring(0, i);
            List<String> matches = new ArrayList<>();
            for (String p : products) {
                if (p.startsWith(prefix)) {
                    matches.add(p);
                    if (matches.size() == 3) {
                        break;
                    }
                }
            }
            result.add(matches);
        }
        return result;
    }
}
