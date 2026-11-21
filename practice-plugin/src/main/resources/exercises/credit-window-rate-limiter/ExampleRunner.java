package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: config <baseLimit> <windowSeconds> <maxCredits> followed by semicolon-separated allow <clientId> <timestamp>, for example: config 2 10 3; allow api 1; allow api 12; allow api 13; allow api 14; allow api 15";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution(2, 10, 3);
        assertThat(List.of(
                first.allow("api", 1),
                first.allow("api", 12),
                first.allow("api", 13),
                first.allow("api", 14),
                first.allow("api", 15)))
                .as("Example 1")
                .containsExactly(true, true, true, true, false);

        Solution second = new Solution(2, 5, 3);
        assertThat(List.of(
                second.allow("svc", 0),
                second.allow("svc", 20),
                second.allow("svc", 20),
                second.allow("svc", 21),
                second.allow("svc", 22),
                second.allow("svc", 23),
                second.allow("svc", 24)))
                .as("Example 2")
                .containsExactly(true, true, true, true, true, true, false);

        Solution third = new Solution(1, 10, 2);
        assertThat(List.of(
                third.allow("web", 2),
                third.allow("web", 22),
                third.allow("web", 23),
                third.allow("web", 32),
                third.allow("web", 33)))
                .as("Example 3")
                .containsExactly(true, true, true, true, false);

        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            String[] clauses = input.split(";");
            if (clauses.length < 2) {
                throw new IllegalArgumentException();
            }
            String[] header = clauses[0].trim().split("\\s+");
            if (header.length != 4 || !"config".equals(header[0])) {
                throw new IllegalArgumentException();
            }
            Solution limiter = new Solution(
                    Integer.parseInt(header[1]),
                    Integer.parseInt(header[2]),
                    Integer.parseInt(header[3]));
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
