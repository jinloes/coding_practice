package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Solution {
    public String closestCommonGroup(
            String[][] groupLinks, String[][] memberships, String[] targetEmployees) {
        if (targetEmployees.length == 0) {
            return null;
        }
        Map<String, Integer> targetIndex = new HashMap<>();
        for (String emp : targetEmployees) {
            targetIndex.putIfAbsent(emp, targetIndex.size());
        }
        int targetCount = targetIndex.size();

        Map<String, List<String>> children = new HashMap<>();
        Set<String> allGroups = new HashSet<>();
        Set<String> childGroups = new HashSet<>();
        for (String[] link : groupLinks) {
            children.computeIfAbsent(link[0], ignored -> new ArrayList<>()).add(link[1]);
            allGroups.add(link[0]);
            allGroups.add(link[1]);
            childGroups.add(link[1]);
        }

        Map<String, BitSet> directTargets = new HashMap<>();
        boolean[] foundTargets = new boolean[targetCount];
        int foundCount = 0;
        for (String[] m : memberships) {
            allGroups.add(m[0]);
            Integer idx = targetIndex.get(m[1]);
            if (idx != null) {
                directTargets.computeIfAbsent(m[0], ignored -> new BitSet(targetCount)).set(idx);
                if (!foundTargets[idx]) {
                    foundTargets[idx] = true;
                    foundCount++;
                }
            }
        }
        if (foundCount < targetCount) {
            return null;
        }

        String root = null;
        for (String g : allGroups) {
            if (!childGroups.contains(g)) {
                root = g;
                break;
            }
        }
        if (root == null) {
            return null;
        }

        // Iterative post-order traversal to avoid stack overflow on deep org trees.
        List<String> order = new ArrayList<>(allGroups.size());
        Map<String, Integer> depth = new HashMap<>(allGroups.size() * 2);
        Deque<String> stack = new ArrayDeque<>();
        stack.push(root);
        depth.put(root, 0);
        while (!stack.isEmpty()) {
            String node = stack.pop();
            order.add(node);
            int d = depth.get(node);
            for (String child : children.getOrDefault(node, List.of())) {
                depth.put(child, d + 1);
                stack.push(child);
            }
        }

        Map<String, BitSet> covered = new HashMap<>(allGroups.size() * 2);
        String bestGroup = null;
        int bestDepth = -1;
        for (int i = order.size() - 1; i >= 0; i--) {
            String node = order.get(i);
            BitSet bits = new BitSet(targetCount);
            BitSet direct = directTargets.get(node);
            if (direct != null) {
                bits.or(direct);
            }
            for (String child : children.getOrDefault(node, List.of())) {
                BitSet childBits = covered.remove(child);
                if (childBits != null) {
                    bits.or(childBits);
                }
            }
            if (bits.cardinality() == targetCount) {
                int d = depth.get(node);
                if (d > bestDepth || (d == bestDepth && (bestGroup == null || node.compareTo(bestGroup) < 0))) {
                    bestDepth = d;
                    bestGroup = node;
                }
            }
            covered.put(node, bits);
        }
        return bestGroup;
    }
}
