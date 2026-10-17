package com.jinloes.practice;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: n and k separated by space, for example: 4 2";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.combinations(4, 2)).containsExactly(List.of(1, 2), List.of(1, 3), List.of(1, 4), List.of(2, 3), List.of(2, 4), List.of(3, 4));
        assertThat(Solution.combinations(3, 0)).containsExactly(List.of());
        assertThat(Solution.combinations(2, 3)).isEmpty();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] p = input.trim().split("\\s+"); if (p.length != 2) { System.out.println(USAGE); return; } int n=Integer.parseInt(p[0]); int k=Integer.parseInt(p[1]); System.out.println("combinations("+n+", "+k+") = "+Solution.combinations(n,k)); }
        catch (NumberFormatException exception) { System.out.println(USAGE); }
    }
}
