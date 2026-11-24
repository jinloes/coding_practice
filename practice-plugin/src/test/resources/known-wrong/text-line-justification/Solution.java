package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < words.length) {
            int j = i + 1;
            int wordChars = words[i].length();
            while (j < words.length && wordChars + words[j].length() + (j - i) <= maxWidth) {
                wordChars += words[j].length();
                j++;
            }
            int wordCount = j - i;
            int totalSpaces = maxWidth - wordChars;
            StringBuilder line = new StringBuilder(maxWidth);
            // Bug: fully justifies the last line as well when it has 2+ words.
            if (wordCount == 1) {
                line.append(words[i]);
                while (line.length() < maxWidth) {
                    line.append(' ');
                }
            } else {
                int gaps = wordCount - 1;
                int baseSpaces = totalSpaces / gaps;
                int extraSpaces = totalSpaces % gaps;
                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k < j - 1) {
                        int count = baseSpaces + ((k - i) < extraSpaces ? 1 : 0);
                        for (int s = 0; s < count; s++) {
                            line.append(' ');
                        }
                    }
                }
            }
            result.add(line.toString());
            i = j;
        }
        return result;
    }
}
