package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final int maxRequests;
    private final long windowSeconds;
    private final Map<String, Deque<Long>> history = new HashMap<>();

    public Solution(int maxRequests, int windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public boolean allow(String clientId, long timestampSeconds) {
        Deque<Long> window = history.computeIfAbsent(clientId, ignored -> new ArrayDeque<>());
        while (!window.isEmpty() && window.peekFirst() < timestampSeconds - windowSeconds) {
            window.removeFirst();
        }
        window.addLast(timestampSeconds);
        return window.size() <= maxRequests;
    }
}
