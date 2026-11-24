package com.jinloes.practice;

import java.util.HashMap;
import java.util.Map;

public class Solution {
    private final int timeToLive;
    private final Map<String, Integer> expiries = new HashMap<>();

    public Solution(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void generate(String tokenId, int currentTime) {
        expiries.put(tokenId, currentTime + timeToLive);
    }

    public void renew(String tokenId, int currentTime) {
        Integer exp = expiries.get(tokenId);
        // Bug: uses >= instead of >, allowing renewal at the exact expiration timestamp.
        if (exp != null && exp >= currentTime) {
            expiries.put(tokenId, currentTime + timeToLive);
        }
    }

    public int countUnexpired(int currentTime) {
        int count = 0;
        for (int exp : expiries.values()) {
            if (exp > currentTime) {
                count++;
            }
        }
        return count;
    }
}
