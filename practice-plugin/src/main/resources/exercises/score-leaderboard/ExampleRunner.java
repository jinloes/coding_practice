package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: add 1 50, add 2 80, add 3 20, top 2";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution a = new Solution(); a.addScore(1,50); a.addScore(2,80); a.addScore(3,20); assertThat(a.top(2)).isEqualTo(130); Solution b = new Solution(); b.addScore(1,50); b.addScore(2,80); b.addScore(1,25); assertThat(b.top(2)).isEqualTo(155); Solution c = new Solution(); c.addScore(1,75); c.addScore(2,80); c.addScore(3,20); c.reset(2); assertThat(c.top(5)).isEqualTo(95); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { if (input.isBlank() || !input.contains(",")) { System.out.println(USAGE); return; } Solution b = new Solution(); try { for (String raw : input.split(",")) { String[] t = raw.trim().split("\s+"); if (t.length == 3 && t[0].equals("add")) b.addScore(Integer.parseInt(t[1]), Integer.parseInt(t[2])); else if (t.length == 2 && t[0].equals("reset")) b.reset(Integer.parseInt(t[1])); else if (t.length == 2 && t[0].equals("top")) System.out.println("top " + t[1] + " -> " + b.top(Integer.parseInt(t[1]))); else { System.out.println(USAGE); return; } } } catch (RuntimeException e) { System.out.println(USAGE); } }
}
