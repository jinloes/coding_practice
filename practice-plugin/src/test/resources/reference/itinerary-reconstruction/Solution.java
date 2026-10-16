package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
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
        Deque<String> stack = new ArrayDeque<>();
        List<String> reversed = new ArrayList<>(tickets.size() + 1);
        stack.push(start);
        while (!stack.isEmpty()) {
            PriorityQueue<String> next = departures.get(stack.peek());
            if (next == null || next.isEmpty()) {
                reversed.add(stack.pop());
            } else {
                stack.push(next.poll());
            }
        }
        Collections.reverse(reversed);
        return reversed;
    }
}
