package com.jinloes.practice;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: push 4, push 9, pop, peek, size";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution stack = new Solution();
        stack.push(4);
        stack.push(9);
        assertThat(stack.pop()).as("Example 1: pop after push(4), push(9)").isEqualTo(9);
        assertThat(stack.pop()).as("Example 1: pop returns the remaining value").isEqualTo(4);

        Solution peek = new Solution();
        peek.push(12);
        assertThat(peek.peek()).as("Example 2: peek returns the top value").isEqualTo(12);
        assertThat(peek.size()).as("Example 2: peek leaves the value on the stack").isEqualTo(1);

        Solution empty = new Solution();
        assertThat(empty.isEmpty()).as("Example 3: a new stack must be empty").isTrue();
        assertThatThrownBy(empty::pop).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::peek).isInstanceOf(NoSuchElementException.class);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] operations = input.split(",");
        Solution stack = new Solution();
        for (String raw : operations) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            if (!apply(stack, operation)) {
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
                case "peek" -> System.out.println("peek() = " + stack.peek());
                case "size" -> System.out.println("size() = " + stack.size());
                case "isEmpty" -> System.out.println("isEmpty() = " + stack.isEmpty());
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
