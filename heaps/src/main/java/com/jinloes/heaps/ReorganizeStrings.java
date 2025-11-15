package com.jinloes.heaps;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

class ReorganizeStrings {
    public String reorganizeString(String s) {
        Map<Character, Integer> charCount = new HashMap<>();

        for (int i = 0; i < s.length(); i++) {
            int count = charCount.getOrDefault(s.charAt(i), 0);
            charCount.put(s.charAt(i), count + 1);
        }


        PriorityQueue<Map.Entry<Character, Integer>> maxHeap = new PriorityQueue<>(
                Map.Entry.<Character, Integer>comparingByValue().reversed()
        );

        maxHeap.addAll(charCount.entrySet());

        String result = "";
        Map.Entry<Character, Integer> previous = null;

        while (!maxHeap.isEmpty() || previous != null) {
            if (previous != null && maxHeap.isEmpty()) {
                return "";
            }
            Map.Entry<Character, Integer> current = maxHeap.poll();
            result += current.getKey();

            int count = current.getValue();
            current.setValue(count - 1);
            if (previous != null) {
                maxHeap.add(previous);
                previous = null;
            }
            if(current.getValue() > 0) {
                previous = current;
            }
        }

        return result;
    }
}
