package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: list values followed by an optional cycle entry index, for example: [1, 2, 3, 4] cycle 1";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution.Node[] cycle = nodes(1,2,3,4); cycle[3].next = cycle[1]; assertThat(Solution.cycleEntry(cycle[0])).isSameAs(cycle[1]); assertThat(Solution.cycleEntry(nodes(5,6)[0])).isNull(); Solution.Node self = new Solution.Node(9, null); self.next = self; assertThat(Solution.cycleEntry(self)).isSameAs(self); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { try { int cycleAt = -1; String valuesPart = input; int marker = input.indexOf("cycle"); if (marker >= 0) { valuesPart = input.substring(0, marker); cycleAt = Integer.parseInt(input.substring(marker + 5).trim()); } int[] values = parseInts(valuesPart); if (values.length == 0) { System.out.println("Expected at least one node. " + USAGE); return; } Solution.Node[] nodes = nodes(values); if (cycleAt >= 0) { if (cycleAt >= nodes.length) { System.out.println("Cycle index is out of range. " + USAGE); return; } nodes[nodes.length - 1].next = nodes[cycleAt]; } Solution.Node entry = Solution.cycleEntry(nodes[0]); System.out.println(entry == null ? "null" : "node " + entry.value); } catch (NumberFormatException exception) { System.out.println("Could not parse input. " + USAGE); } }
    private static Solution.Node[] nodes(int... values) { Solution.Node[] nodes = new Solution.Node[values.length]; for (int i = 0; i < values.length; i++) nodes[i] = new Solution.Node(values[i], null); for (int i = 0; i + 1 < nodes.length; i++) nodes[i].next = nodes[i + 1]; return nodes; }
    private static int[] parseInts(String input) { String cleaned = input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if (cleaned.isEmpty()) return new int[0]; String[] tokens = cleaned.split("\s+"); int[] values = new int[tokens.length]; for (int i = 0; i < tokens.length; i++) values[i] = Integer.parseInt(tokens[i]); return values; }
}
