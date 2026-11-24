package com.jinloes.practice;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Solution {
    private final int timeToLive;
    private final LinkedHashMap<String, Integer> expiries = new LinkedHashMap<>();

    public Solution(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    public void generate(String tokenId, int currentTime) {
        evictExpired(currentTime);
        expiries.put(tokenId, currentTime + timeToLive);
    }

    public void renew(String tokenId, int currentTime) {
        evictExpired(currentTime);
        if (expiries.containsKey(tokenId)) {
            expiries.remove(tokenId);
            expiries.put(tokenId, currentTime + timeToLive);
        }
    }

    public int countUnexpired(int currentTime) {
        evictExpired(currentTime);
        return expiries.size();
    }

    private void evictExpired(int currentTime) {
        Iterator<Map.Entry<String, Integer>> it = expiries.entrySet().iterator();
        while (it.hasNext() && it.next().getValue() <= currentTime) {
            it.remove();
        }
    }
}
