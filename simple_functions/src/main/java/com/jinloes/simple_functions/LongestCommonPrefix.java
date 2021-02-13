package com.jinloes.simple_functions;

public class LongestCommonPrefix {
    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }
        int smallestStrIdx = 0;
        int smallestStrLen = strs[0].length();
        for (int i = 1; i < strs.length; i++) {
            if (strs[i].length() < smallestStrLen) {
                smallestStrLen = strs[i].length();
                smallestStrIdx = i;
            }
        }

        String shortest = strs[smallestStrIdx];

        int i = 0;
        for (; i < shortest.length(); i++) {
            for (int j = 0; j < strs.length; j++) {
                if (strs[j].charAt(i) != shortest.charAt(i)) {
                    return shortest.substring(0, i);
                }
            }
        }
        return shortest.substring(0, i);
    }
}
