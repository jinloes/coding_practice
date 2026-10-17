package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: push 5, push 1, push 5, top, popMax, top";

    private ExampleRunner() {}

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution stack = new Solution();
        stack.push(5); stack.push(1); stack.push(5);
        assertThat(stack.top()).isEqualTo(5);
        assertThat(stack.popMax()).isEqualTo(5);
        assertThat(stack.top()).isEqualTo(1);
        assertThat(stack.peekMax()).isEqualTo(5);
        assertThat(stack.pop()).isEqualTo(1);
        Solution duplicate = new Solution();
        duplicate.push(-2); duplicate.push(7); duplicate.push(7);
        assertThat(duplicate.popMax()).isEqualTo(7);
        assertThat(duplicate.popMax()).isEqualTo(7);
        assertThat(duplicate.pop()).isEqualTo(-2);
        System.out.println("Examples passed: 2");
    }

    private static void runInput(String input) {
        Solution stack = new Solution();
        List<String> outputs = new ArrayList<>();
        try {
            for (String raw : input.split(",")) {
                String[] parts = raw.trim().split("\s+");
                if (parts.length == 2 && parts[0].equals("push")) {
                    stack.push(Integer.parseInt(parts[1]));
                } else if (parts.length == 1 && parts[0].equals("pop")) {
                    outputs.add(String.valueOf(stack.pop()));
                } else if (parts.length == 1 && parts[0].equals("top")) {
                    outputs.add(String.valueOf(stack.top()));
                } else if (parts.length == 1 && parts[0].equals("peekMax")) {
                    outputs.add(String.valueOf(stack.peekMax()));
                } else if (parts.length == 1 && parts[0].equals("popMax")) {
                    outputs.add(String.valueOf(stack.popMax()));
                } else {
                    System.out.println("Unknown operation. " + USAGE);
                    return;
                }
            }
            System.out.println(outputs);
        } catch (RuntimeException exception) {
            System.out.println("Could not run operations. " + USAGE);
        }
    }
}
