package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Solution {
    public static int[] sortItems(int itemCount, int groupCount, int[] groups, List<List<Integer>> beforeItems) {
        int[] itemGroup = groups.clone();
        int nextGroup = groupCount;
        for (int i = 0; i < itemCount; i++) if (itemGroup[i] == -1) itemGroup[i] = nextGroup++;
        List<List<Integer>> groupGraph = graph(nextGroup);
        int[] groupIn = new int[nextGroup];
        List<List<Integer>> itemGraph = graph(itemCount);
        int[] itemIn = new int[itemCount];
        for (int item = 0; item < itemCount; item++) {
            for (int before : beforeItems.get(item)) {
                if (itemGroup[before] == itemGroup[item]) { itemGraph.get(before).add(item); itemIn[item]++; }
                else { groupGraph.get(itemGroup[before]).add(itemGroup[item]); groupIn[itemGroup[item]]++; }
            }
        }
        List<Integer> groupOrder = topo(groupGraph, groupIn);
        if (groupOrder.size() != nextGroup) return new int[0];
        Map<Integer, List<Integer>> itemsByGroup = new HashMap<>();
        for (int item = 0; item < itemCount; item++) itemsByGroup.computeIfAbsent(itemGroup[item], ignored -> new ArrayList<>()).add(item);
        int[] itemInWorking = itemIn.clone();
        List<Integer> result = new ArrayList<>();
        for (int group : groupOrder) {
            List<Integer> items = itemsByGroup.getOrDefault(group, List.of());
            List<Integer> ordered = topoSubset(items, itemGraph, itemInWorking);
            if (ordered.size() != items.size()) return new int[0];
            result.addAll(ordered);
        }
        return result.stream().mapToInt(Integer::intValue).toArray();
    }
    private static List<List<Integer>> graph(int size) { List<List<Integer>> graph = new ArrayList<>(); for (int i = 0; i < size; i++) graph.add(new ArrayList<>()); return graph; }
    private static List<Integer> topo(List<List<Integer>> graph, int[] indegree) { Queue<Integer> ready = new ArrayDeque<>(); for (int i = 0; i < indegree.length; i++) if (indegree[i] == 0) ready.add(i); List<Integer> order = new ArrayList<>(); while (!ready.isEmpty()) { int node = ready.remove(); order.add(node); for (int next : graph.get(node)) if (--indegree[next] == 0) ready.add(next); } return order; }
    private static List<Integer> topoSubset(List<Integer> items, List<List<Integer>> graph, int[] indegree) { Queue<Integer> ready = new ArrayDeque<>(); Set<Integer> allowed = new HashSet<>(items); for (int item : items) if (indegree[item] == 0) ready.add(item); List<Integer> order = new ArrayList<>(); while (!ready.isEmpty()) { int item = ready.remove(); order.add(item); for (int next : graph.get(item)) if (allowed.contains(next) && --indegree[next] == 0) ready.add(next); } return order; }
}

