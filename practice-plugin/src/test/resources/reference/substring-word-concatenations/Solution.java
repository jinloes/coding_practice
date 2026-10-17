package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Solution {
    public static List<Integer> findStarts(String text, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (words.length == 0) return result;
        int wordLength = words[0].length();
        int totalWords = words.length;
        int totalLength = wordLength * totalWords;
        if (totalLength > text.length()) return result;
        Map<String, Integer> required = new HashMap<>();
        for (String word : words) required.merge(word, 1, Integer::sum);
        for (int offset = 0; offset < wordLength; offset++) {
            Map<String, Integer> seen = new HashMap<>();
            int left = offset;
            int count = 0;
            for (int right = offset; right + wordLength <= text.length(); right += wordLength) {
                String word = text.substring(right, right + wordLength);
                if (!required.containsKey(word)) { seen.clear(); count = 0; left = right + wordLength; continue; }
                seen.merge(word, 1, Integer::sum);
                count++;
                while (seen.get(word) > required.get(word)) {
                    String removed = text.substring(left, left + wordLength);
                    seen.merge(removed, -1, Integer::sum);
                    left += wordLength;
                    count--;
                }
                if (count == totalWords) {
                    result.add(left);
                    String removed = text.substring(left, left + wordLength);
                    seen.merge(removed, -1, Integer::sum);
                    left += wordLength;
                    count--;
                }
            }
        }
        java.util.Collections.sort(result);
        return result;
    }
}
