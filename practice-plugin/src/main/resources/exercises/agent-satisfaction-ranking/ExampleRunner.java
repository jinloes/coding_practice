package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: semicolon-separated operations rate <agent> <1..5> and query <minRatings>, for example: rate mia 5; rate ben 4; rate mia 4; rate ben 5; rate ben 5; query 2";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution();
        first.recordScore("mia", 5);
        first.recordScore("ben", 4);
        first.recordScore("mia", 4);
        first.recordScore("ben", 5);
        first.recordScore("ben", 5);
        assertThat(first.qualifiedAgents(2)).as("Example 1").containsExactly("ben", "mia");

        Solution second = new Solution();
        second.recordScore("zoe", 4);
        second.recordScore("zoe", 4);
        second.recordScore("ava", 4);
        second.recordScore("kai", 5);
        assertThat(second.qualifiedAgents(2)).as("Example 2").containsExactly("zoe");

        Solution third = new Solution();
        third.recordScore("bob", 5);
        third.recordScore("bob", 3);
        third.recordScore("ada", 4);
        third.recordScore("ada", 4);
        third.recordScore("cam", 4);
        assertThat(third.qualifiedAgents(1)).as("Example 3").containsExactly("ada", "bob", "cam");

        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            Solution tracker = new Solution();
            List<String> last = null;
            for (String rawOp : input.split(";")) {
                String[] parts = rawOp.trim().split("\\s+");
                if (parts.length == 3 && "rate".equals(parts[0])) {
                    tracker.recordScore(parts[1], Integer.parseInt(parts[2]));
                } else if (parts.length == 2 && "query".equals(parts[0])) {
                    last = tracker.qualifiedAgents(Integer.parseInt(parts[1]));
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (last == null) {
                throw new IllegalArgumentException();
            }
            System.out.println("qualifiedAgents = " + last);
        } catch (RuntimeException exception) {
            System.out.println("Could not read operations. " + USAGE);
        }
    }
}
