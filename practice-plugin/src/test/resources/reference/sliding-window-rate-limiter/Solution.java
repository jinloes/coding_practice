package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final int maxRequests;
    private final long windowSeconds;
    private final Map<String, Deque<Long>> acceptedByClient = new HashMap<>();

    public Solution(int maxRequests, int windowSeconds) {
        if (maxRequests < 1 || windowSeconds < 1) {
            throw new IllegalArgumentException("Limits must be positive.");
        }
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public boolean allow(String clientId, long timestampSeconds) {
        if (clientId == null || clientId.isBlank() || timestampSeconds < 0) {
            throw new IllegalArgumentException("Invalid request arguments.");
        }
        Deque<Long> window = acceptedByClient.computeIfAbsent(clientId, ignored -> new ArrayDeque<>());
        long cutoff = timestampSeconds - windowSeconds;
        while (!window.isEmpty() && window.peekFirst() <= cutoff) {
            window.removeFirst();
        }
        if (window.size() >= maxRequests) {
            return false;
        }
        window.addLast(timestampSeconds);
        return true;
    }
}
