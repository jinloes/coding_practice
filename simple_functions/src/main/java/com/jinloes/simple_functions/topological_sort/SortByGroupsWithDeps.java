package com.jinloes.simple_functions.topological_sort;

import java.util.*;

/**
 * There are n items each belonging to zero or one of m groups where group[i] is the group that the i-th item
 * belongs to and it's equal to -1 if the i-th item belongs to no group.
 * The items and the groups are zero indexed. A group can have no item belonging to it.
 * <p>
 * Return a sorted list of the items such that:
 * <p>
 * The items that belong to the same group are next to each other in the sorted list.
 * There are some relations between these items where beforeItems[i] is a list containing all the items that should
 * come before the i-th item in the sorted array (to the left of the i-th item).
 * Return any solution if there is more than one solution and return an empty list if there is no solution.
 */
public class SortByGroupsWithDeps {
    public int[] sortItems(int n, int m, int[] group, List<List<Integer>> beforeItems) {
        Map<Integer, Set<Integer>> groupToItems = new HashMap<>();
        Map<Integer, Set<Integer>> groupIncomingEdges = new HashMap<>();

        int nonGroups = 0;
        for (int i = 0; i < group.length; i++) {
            if (group[i] == -1) {
                group[i] = -1 - nonGroups;
                nonGroups++;
            }
            groupIncomingEdges.putIfAbsent(group[i], new HashSet<>());
        }

        for (int i = 0; i < n; i++) {
            int groupId = group[i];

            for (int item : beforeItems.get(i)) {
                if (groupId != group[item]) {
                    groupIncomingEdges.get(groupId).add(group[item]);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            int itemGroup = group[i];
            groupToItems.putIfAbsent(itemGroup, new HashSet<>());
            groupToItems.get(itemGroup).add(i);
        }

        List<Integer> sortedGroup = topSort(groupIncomingEdges);

        if (!groupIncomingEdges.isEmpty()) {
            // Cycle return empty array
            return new int[]{};
        }

        List<Integer> result = new ArrayList<>();
        for (int groupId : sortedGroup) {
            Map<Integer, Set<Integer>> groupItemGraph = buildGroupItemGraph(groupToItems.get(groupId), beforeItems);
            List<Integer> sortedGroupsItems = topSort(groupItemGraph);
            if (!groupItemGraph.isEmpty()) {
                return new int[]{};
            }
            result.addAll(sortedGroupsItems);
        }

        return result.stream().mapToInt(x -> x).toArray();
    }

    private Map<Integer, Set<Integer>> buildGroupItemGraph(Set<Integer> groupItems, List<List<Integer>> beforeItems) {
        Map<Integer, Set<Integer>> graph = new HashMap<>();
        for (int groupItem : groupItems) {
            graph.putIfAbsent(groupItem, new HashSet<>());
            for (int item : beforeItems.get(groupItem)) {
                if (groupItems.contains(item)) {
                    graph.get(groupItem).add(item);
                }
            }
        }
        return graph;
    }

    private void addEmpty(Queue<Integer> queue, Iterable<Map.Entry<Integer, Set<Integer>>> iterable) {
        Iterator<Map.Entry<Integer, Set<Integer>>> it = iterable.iterator();

        while (it.hasNext()) {
            Map.Entry<Integer, Set<Integer>> entry = it.next();
            if (entry.getValue().isEmpty()) {
                queue.add(entry.getKey());
                it.remove();
            }
        }
    }

    private List<Integer> topSort(Map<Integer, Set<Integer>> vertexIncomingEdges) {
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> sortedValues = new ArrayList<>();
        Iterator<Map.Entry<Integer, Set<Integer>>> it;

        addEmpty(queue, vertexIncomingEdges.entrySet());

        while (!queue.isEmpty()) {
            Integer currentVertex = queue.poll();
            sortedValues.add(currentVertex);
            it = vertexIncomingEdges.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Set<Integer>> current = it.next();
                Set<Integer> incomingSet = current.getValue();
                incomingSet.remove(currentVertex);
                if (incomingSet.isEmpty()) {
                    queue.add(current.getKey());
                    it.remove();
                }
            }
        }
        return sortedValues;
    }
}
