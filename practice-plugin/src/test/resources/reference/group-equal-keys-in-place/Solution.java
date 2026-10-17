package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    public static void groupEqualKeys(int[] keys) {
        Map<Integer, Integer> counts = new HashMap<>();
        for (int key : keys) counts.merge(key, 1, Integer::sum);
        int write = 0;
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) for (int i = 0; i < entry.getValue(); i++) keys[write++] = entry.getKey();
    }
}
