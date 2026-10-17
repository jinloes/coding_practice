package com.jinloes.practice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: item count, group count, groups, and prerequisite lists, for example: n=8; m=2; group=[-1,-1,1,0,0,1,0,-1]; before=[[],[6],[5],[6],[3,6],[],[],[]]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args)); return; }
        int[] groups = {-1,-1,1,0,0,1,0,-1};
        List<List<Integer>> before = List.of(List.of(), List.of(6), List.of(5), List.of(6), List.of(3,6), List.of(), List.of(), List.of());
        assertThat(Solution.sortItems(8, 2, groups, before)).hasSize(8);
        assertThat(Solution.sortItems(2, 1, new int[]{0,0}, List.of(List.of(1), List.of(0)))).isEmpty();
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try {
            String[] parts = input.split(";\\s*");
            if (parts.length != 4) throw new IllegalArgumentException();
            int n = Integer.parseInt(parts[0].replace("n=", "").trim());
            int m = Integer.parseInt(parts[1].replace("m=", "").trim());
            int[] groups = parseArray(parts[2].replace("group=", "").trim());
            List<List<Integer>> before = parseLists(parts[3].replace("before=", "").trim());
            System.out.println(Arrays.toString(Solution.sortItems(n, m, groups, before)));
        } catch (RuntimeException exception) { System.out.println(USAGE); }
    }
    private static int[] parseArray(String text) { String body = text.substring(1, text.length() - 1).trim(); if (body.isEmpty()) return new int[0]; return Arrays.stream(body.split("\\s*,\\s*")).mapToInt(Integer::parseInt).toArray(); }
    private static List<List<Integer>> parseLists(String text) {
        String body = text.trim();
        if (!body.startsWith("[") || !body.endsWith("]")) throw new IllegalArgumentException();
        body = body.substring(1, body.length() - 1);
        List<List<Integer>> result = new ArrayList<>();
        int index = 0;
        while (index < body.length()) {
            if (body.charAt(index) != '[') throw new IllegalArgumentException();
            int end = body.indexOf(']', index);
            String part = body.substring(index + 1, end).trim();
            if (part.isEmpty()) result.add(List.of()); else result.add(Arrays.stream(part.split("\\s*,\\s*")).map(Integer::parseInt).toList());
            index = end + 1;
            if (index < body.length() && body.charAt(index) == ',') index++;
        }
        return result;
    }
}
