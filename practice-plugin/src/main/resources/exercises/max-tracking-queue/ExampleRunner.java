package com.jinloes.practice;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: enqueue 3, enqueue 1, enqueue 5, max, dequeue, max";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution q = new Solution(); q.enqueue(3); q.enqueue(1); q.enqueue(5); assertThat(q.max()).isEqualTo(5); assertThat(q.dequeue()).isEqualTo(3); assertThat(q.max()).isEqualTo(5); Solution d = new Solution(); d.enqueue(4); d.enqueue(4); assertThat(d.dequeue()).isEqualTo(4); assertThat(d.max()).isEqualTo(4); assertThatThrownBy(() -> new Solution().max()).isInstanceOf(NoSuchElementException.class); System.out.println("Examples passed: 3"); }
    private static void runInput(String input) { Solution q = new Solution(); try { for (String raw : input.split(",")) { String op = raw.trim(); if (op.isEmpty()) continue; String[] parts = op.split("\s+"); if (parts[0].equals("enqueue") && parts.length == 2) { int value = Integer.parseInt(parts[1]); q.enqueue(value); System.out.println("enqueue(" + value + ")"); } else if (parts[0].equals("dequeue") && parts.length == 1) { System.out.println("dequeue() = " + q.dequeue()); } else if (parts[0].equals("max") && parts.length == 1) { System.out.println("max() = " + q.max()); } else { System.out.println("Unknown operation. " + USAGE); return; } } } catch (RuntimeException exception) { System.out.println("Could not run operations. " + USAGE); } }
}
