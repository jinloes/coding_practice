package com.jinloes.simple_functions.tree;

import java.util.*;

/**
 * A company has n employees with a unique ID for each employee from 0 to n - 1. The head of the company is the one with headID.
 * <p>
 * Each employee has one direct manager given in the manager array where manager[i] is the direct manager of the i-th employee, manager[headID] = -1. Also, it is guaranteed that the subordination relationships have a tree structure.
 * <p>
 * The head of the company wants to inform all the company employees of an urgent piece of news. He will inform his direct subordinates, and they will inform their subordinates, and so on until all employees know about the urgent news.
 * <p>
 * The i-th employee needs informTime[i] minutes to inform all of his direct subordinates (i.e., After informTime[i] minutes, all his direct subordinates can start spreading the news).
 * <p>
 * Return the number of minutes needed to inform all the employees about the urgent news.
 */
public class NumOfMinutes {

    public int numOfMinutesBfs(int n, int headID, int[] manager, int[] informTime) {
        Map<Integer, Set<Integer>> managerToEmployees = new HashMap<>();

        for (int i = 0; i < manager.length; i++) {
            managerToEmployees.putIfAbsent(manager[i], new HashSet<>());
            managerToEmployees.get(manager[i]).add(i);
        }

        Queue<Integer> employees = new ArrayDeque<>(managerToEmployees.get(-1));

        int totalTime = 0;
        int[] messageTime = new int[informTime.length];
        while (!employees.isEmpty()) {
            int current = employees.poll();

            for (int report : managerToEmployees.getOrDefault(current, new HashSet<>())) {
                employees.add(report);
                int time = messageTime[current] + informTime[current];
                messageTime[report] = time;
                totalTime = Math.max(totalTime, time);
            }
        }

        return totalTime;

    }

    public int numOfMinutesDfs(int n, int headID, int[] manager, int[] informTime) {
        Map<Integer, Set<Integer>> reporteeMap = new HashMap<>();

        for (int i = 0; i < manager.length; i++) {
            reporteeMap.putIfAbsent(manager[i], new HashSet<>());
            reporteeMap.get(manager[i]).add(i);
        }

        return dfs(headID, informTime, reporteeMap);
    }

    private int dfs(int currentEmployee, int[] informTime, Map<Integer, Set<Integer>> reporteeMap) {
        int timeToReport = informTime[currentEmployee];

        int reporteeTime = 0;

        for (Integer reportee : reporteeMap.getOrDefault(currentEmployee, new HashSet<>())) {
            reporteeTime = Math.max(reporteeTime, dfs(reportee, informTime, reporteeMap));
        }

        return timeToReport + reporteeTime;
    }
}
