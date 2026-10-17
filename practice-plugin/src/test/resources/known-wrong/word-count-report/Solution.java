package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<String> countWords(String text) {
        List<String> rows = new ArrayList<>();
        for (String token : text.split("\s+")) if (!token.isBlank()) rows.add("1 " + token);
        return rows;
    }
}
