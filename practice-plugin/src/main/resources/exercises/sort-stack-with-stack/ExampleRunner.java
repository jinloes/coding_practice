package com.jinloes.practice;

import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: stack values from bottom to top, for example: [3, 1, 2]";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        Stack<Integer> first = stackOf(3, 1, 2);
        Solution.sort(first);
        assertThat(first).containsExactly(3, 2, 1);
        Stack<Integer> second = stackOf(4, 4, -1);
        Solution.sort(second);
        assertThat(second).containsExactly(4, 4, -1);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try {
            Stack<Integer> stack = parse(input);
            Solution.sort(stack);
            System.out.println(stack);
        } catch (RuntimeException exception) {
            System.out.println("Could not parse stack. " + USAGE);
        }
    }
    private static Stack<Integer> parse(String text) {
        String body = text.trim();
        if (!body.startsWith("[") || !body.endsWith("]")) throw new IllegalArgumentException();
        Stack<Integer> stack = new Stack<>();
        body = body.substring(1, body.length() - 1).trim();
        if (!body.isEmpty()) for (String part : body.split(",")) stack.push(Integer.parseInt(part.trim()));
        return stack;
    }
    private static Stack<Integer> stackOf(int... values) { Stack<Integer> stack = new Stack<>(); for (int value : values) stack.push(value); return stack; }
}
