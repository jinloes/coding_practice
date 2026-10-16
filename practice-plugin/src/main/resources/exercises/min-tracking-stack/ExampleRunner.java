package com.jinloes.practice;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: push 5, push 2, push 7, min, top, pop, pop, min";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution();
        first.push(5);
        first.push(2);
        first.push(7);
        assertThat(first.min()).as("Example 1: min after push 5, 2, 7").isEqualTo(2);
        assertThat(first.top()).as("Example 1: top after push 5, 2, 7").isEqualTo(7);

        Solution second = new Solution();
        second.push(5);
        second.push(2);
        second.push(7);
        assertThat(second.pop()).as("Example 2: first pop").isEqualTo(7);
        assertThat(second.pop()).as("Example 2: second pop").isEqualTo(2);
        assertThat(second.min()).as("Example 2: min after two pops").isEqualTo(5);

        Solution empty = new Solution();
        assertThatThrownBy(empty::min).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::top).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::pop).isInstanceOf(NoSuchElementException.class);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution stack = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (!operation.isEmpty() && !apply(stack, operation)) {
                return;
            }
        }
    }

    private static boolean apply(Solution stack, String operation) {
        String[] parts = operation.split("\\s+");
        String name = parts[0];
        try {
            switch (name) {
                case "push" -> {
                    if (parts.length != 2) {
                        System.out.println("'" + operation + "' needs exactly one value. " + USAGE);
                        return false;
                    }
                    int value = Integer.parseInt(parts[1]);
                    stack.push(value);
                    System.out.println("push(" + value + ")");
                }
                case "pop" -> System.out.println("pop() = " + stack.pop());
                case "top" -> System.out.println("top() = " + stack.top());
                case "min" -> System.out.println("min() = " + stack.min());
                case "size" -> System.out.println("size() = " + stack.size());
                default -> {
                    System.out.println("Unknown operation '" + operation + "'. " + USAGE);
                    return false;
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("'" + operation + "' does not name an integer. " + USAGE);
            return false;
        } catch (NoSuchElementException exception) {
            System.out.println(name + "() threw NoSuchElementException because the stack is empty");
        }
        return true;
    }
}
