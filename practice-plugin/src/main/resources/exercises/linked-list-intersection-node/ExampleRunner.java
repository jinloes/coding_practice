package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: shared tail, first prefix, second prefix separated by semicolons, for example: [8, 9]; [1, 2]; [3]";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution.Node shared = list(8,9); assertThat(Solution.firstIntersection(append(list(1,2), shared), append(list(3), shared))).isSameAs(shared); assertThat(Solution.firstIntersection(list(1,2), list(1,2))).isNull(); Solution.Node head = list(4,5); assertThat(Solution.firstIntersection(head, head)).isSameAs(head); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { String[] parts = input.split(";"); if (parts.length != 3) { System.out.println("Expected three semicolon-separated lists. " + USAGE); return; } Solution.Node shared = list(parseInts(parts[0])); Solution.Node first = append(list(parseInts(parts[1])), shared); Solution.Node second = append(list(parseInts(parts[2])), shared); Solution.Node result = Solution.firstIntersection(first, second); System.out.println(result == null ? "null" : "node " + result.value); } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
    private static Solution.Node list(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] out = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) out[i] = Integer.parseInt(tokens[i]); return out; }
}
