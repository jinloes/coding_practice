package com.jinloes.practice;

public class Solution {
    public static String alienOrder(String[] words) {
        boolean[] present = new boolean[26];
        for (String word : words) {
            for (int i = 0; i < word.length(); i++) {
                present[word.charAt(i) - 'a'] = true;
            }
        }
        boolean[][] before = new boolean[26][26];
        int[] incoming = new int[26];
        for (int i = 1; i < words.length; i++) {
            String first = words[i - 1];
            String second = words[i];
            int shared = Math.min(first.length(), second.length());
            int j = 0;
            while (j < shared && first.charAt(j) == second.charAt(j)) {
                j++;
            }
            if (j == shared) {
                continue;
            }
            int from = first.charAt(j) - 'a';
            int to = second.charAt(j) - 'a';
            if (!before[from][to]) {
                before[from][to] = true;
                incoming[to]++;
            }
        }
        int[] queue = new int[26];
        int head = 0;
        int tail = 0;
        int total = 0;
        for (int letter = 0; letter < 26; letter++) {
            if (present[letter]) {
                total++;
                if (incoming[letter] == 0) {
                    queue[tail++] = letter;
                }
            }
        }
        StringBuilder order = new StringBuilder();
        while (head < tail) {
            int letter = queue[head++];
            order.append((char) ('a' + letter));
            for (int next = 0; next < 26; next++) {
                if (before[letter][next] && --incoming[next] == 0) {
                    queue[tail++] = next;
                }
            }
        }
        return order.length() == total ? order.toString() : "";
    }
}
