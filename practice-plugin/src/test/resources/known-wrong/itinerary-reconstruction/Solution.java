package com.jinloes.practice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class Solution {
    public static List<String> buildItinerary(List<List<String>> tickets, String start) {
        Map<String, PriorityQueue<String>> departures = new HashMap<>();
        for (List<String> ticket : tickets) {
            departures.computeIfAbsent(ticket.get(0), key -> new PriorityQueue<>()).add(ticket.get(1));
        }
        List<String> route = new ArrayList<>();
        String current = start;
        route.add(current);
        PriorityQueue<String> next = departures.get(current);
        while (next != null && !next.isEmpty()) {
            current = next.poll();
            route.add(current);
            next = departures.get(current);
        }
        return route;
    }
}
