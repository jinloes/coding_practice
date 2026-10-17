package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE = "Input: list values followed by the zero-based node index to delete, for example: [1, 2, 3, 4] 1";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution.Node a = list(1,2,3,4); Solution.deleteNode(a.next); assertThat(values(a)).containsExactly(1,3,4); Solution.Node b = list(7,8); Solution.deleteNode(b); assertThat(values(b)).containsExactly(8); Solution.Node c = list(1,2); assertThatThrownBy(() -> Solution.deleteNode(c.next)).isInstanceOf(IllegalArgumentException.class); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int[] values = parseInts(input); if (values.length < 3) { System.out.println("Need at least two list values and an index. " + USAGE); return; } int index = values[values.length - 1]; int[] listValues = Arrays.copyOf(values, values.length - 1); Solution.Node[] nodes = nodes(listValues); if (index < 0 || index >= nodes.length) { System.out.println("Index out of range. " + USAGE); return; } Solution.deleteNode(nodes[index]); System.out.println(Arrays.toString(values(nodes[0]))); } catch (RuntimeException exception) { System.out.println("Could not perform deletion. " + USAGE); } }
    private static Solution.Node list(int... values) { Solution.Node[] nodes = nodes(values); return nodes.length == 0 ? null : nodes[0]; }
    private static Solution.Node[] nodes(int... values) { Solution.Node[] nodes = new Solution.Node[values.length]; for (int i = 0; i < values.length; i++) nodes[i] = new Solution.Node(values[i], null); for (int i = 0; i + 1 < values.length; i++) nodes[i].next = nodes[i + 1]; return nodes; }
    private static int[] values(Solution.Node head) { int n = 0; for (Solution.Node p = head; p != null; p = p.next) n++; int[] out = new int[n]; for (int i = 0; head != null; head = head.next) out[i++] = head.value; return out; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] out = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) out[i] = Integer.parseInt(tokens[i]); return out; }
}
