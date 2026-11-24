package com.jinloes.practice;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Solution {
    public String closestCommonGroup(
            String[][] groupLinks, String[][] memberships, String[] targetEmployees) {
        Map<String, String> parent = new HashMap<>();
        for (String[] link : groupLinks) {
            parent.put(link[1], link[0]);
        }
        // Bug: only remembers the first group per employee, ignoring multi-group membership.
        Map<String, String> firstGroup = new HashMap<>();
        for (String[] m : memberships) {
            firstGroup.putIfAbsent(m[1], m[0]);
        }
        String current = firstGroup.get(targetEmployees[0]);
        if (current == null) {
            return null;
        }
        for (int i = 1; i < targetEmployees.length; i++) {
            String other = firstGroup.get(targetEmployees[i]);
            if (other == null) {
                return null;
            }
            Set<String> ancestors = new HashSet<>();
            for (String g = current; g != null; g = parent.get(g)) {
                ancestors.add(g);
            }
            while (other != null && !ancestors.contains(other)) {
                other = parent.get(other);
            }
            current = other;
            if (current == null) {
                return null;
            }
        }
        return current;
    }
}
