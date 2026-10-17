package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: one named scenario, for example: shared-cycle";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution.Node shared = chain(8,9); assertThat(Solution.firstIntersection(append(chain(1,2), shared), append(chain(3), shared))).isSameAs(shared); Fixture f = sharedCycleOnly(); assertThat(Solution.firstIntersection(f.first, f.second)).isSameAs(f.expected); assertThat(Solution.firstIntersection(cycle(1,2,3), cycle(4,5,6))).isNull(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (!input.equals("shared-cycle")) { System.out.println("Unknown scenario. " + USAGE); return; } Fixture f = sharedCycleOnly(); Solution.Node result = Solution.firstIntersection(f.first, f.second); System.out.println(result == null ? "null" : "node " + result.value); }

    private record Fixture(Solution.Node first, Solution.Node second, Solution.Node expected) {}
    private static Fixture sharedCycleOnly() { Solution.Node e1 = new Solution.Node(10, null); Solution.Node e2 = new Solution.Node(11, null); Solution.Node e3 = new Solution.Node(12, null); e1.next = e2; e2.next = e3; e3.next = e1; return new Fixture(append(chain(1,2), e1), append(chain(3), e2), e1); }
    private static Solution.Node cycle(int... values) { Solution.Node head = chain(values); Solution.Node p = head; while (p.next != null) p = p.next; p.next = head; return head; }
    private static Solution.Node chain(int... values) { Solution.Node head = null; for (int i = values.length - 1; i >= 0; i--) head = new Solution.Node(values[i], head); return head; }
    private static Solution.Node append(Solution.Node prefix, Solution.Node tail) { if (prefix == null) return tail; Solution.Node p = prefix; while (p.next != null) p = p.next; p.next = tail; return prefix; }
}
