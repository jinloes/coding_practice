package com.jinloes.practice;

public class Solution {
    public static int minSlots(String tasks, int cooldown) {
        if (cooldown < 0) {
            throw new IllegalArgumentException("cooldown must not be negative");
        }
        int[] counts = new int[26];
        int busiest = 0;
        for (int i = 0; i < tasks.length(); i++) {
            busiest = Math.max(busiest, ++counts[tasks.charAt(i) - 'A']);
        }
        int tied = 0;
        for (int count : counts) {
            if (count == busiest) {
                tied++;
            }
        }
        if (busiest == 0) {
            return 0;
        }
        return (busiest - 1) * (cooldown + 1) + tied;
    }
}
