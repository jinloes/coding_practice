package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: limit <maxRequests> <windowSeconds> followed by semicolon-separated allow <clientId> <timestamp>, for example: limit 2 10; allow api 1; allow api 5; allow api 10; allow api 11";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution(2, 10);
        assertThat(List.of(
                first.allow("api", 1),
                first.allow("api", 5),
                first.allow("api", 10),
                first.allow("api", 11)))
                .as("Example 1")
                .containsExactly(true, true, false, true);

        Solution second = new Solution(1, 5);
        assertThat(List.of(
                second.allow("alice", 0),
                second.allow("bob", 0),
                second.allow("alice", 4),
                second.allow("alice", 5)))
                .as("Example 2")
                .containsExactly(true, true, false, true);

        Solution third = new Solution(3, 4);
        assertThat(List.of(
                third.allow("svc", 2),
                third.allow("svc", 2),
                third.allow("svc", 2),
                third.allow("svc", 2),
                third.allow("svc", 6)))
                .as("Example 3")
                .containsExactly(true, true, true, false, true);

        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] clauses = input.split(";");
            if (clauses.length < 2) {
                throw new IllegalArgumentException();
            }
            String[] header = clauses[0].trim().split("\\s+");
            if (header.length != 3 || !"limit".equals(header[0])) {
                throw new IllegalArgumentException();
            }
            Solution limiter = new Solution(Integer.parseInt(header[1]), Integer.parseInt(header[2]));
            List<Boolean> decisions = new ArrayList<>();
            for (int i = 1; i < clauses.length; i++) {
                String[] parts = clauses[i].trim().split("\\s+");
                if (parts.length != 3 || !"allow".equals(parts[0])) {
                    throw new IllegalArgumentException();
                }
                decisions.add(limiter.allow(parts[1], Long.parseLong(parts[2])));
            }
            System.out.println("decisions = " + decisions);
        } catch (RuntimeException exception) {
            System.out.println("Could not read operations. " + USAGE);
        }
    }
}
