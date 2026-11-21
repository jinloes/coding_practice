package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: semicolon-separated reg <pattern> <handler> and match <path>, for example: reg /projects/*/issues listIssues; reg /projects/core/issues coreIssues; match /projects/core/issues; match /projects/ops/issues";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution first = new Solution();
        first.register("/projects/*/issues", "listIssues");
        first.register("/projects/core/issues", "coreIssues");
        assertThat(List.of(first.match("/projects/core/issues"), first.match("/projects/ops/issues")))
                .as("Example 1")
                .containsExactly("coreIssues", "listIssues");

        Solution second = new Solution();
        second.register("/api/*/members", "wildcardFirst");
        second.register("/api/v1/*", "literalFirst");
        assertThat(List.of(second.match("/api/v1/members")))
                .as("Example 2")
                .containsExactly("literalFirst");

        Solution third = new Solution();
        third.register("/", "home");
        assertThat(List.of(third.match("/"), third.match("/unknown")))
                .as("Example 3")
                .containsExactly("home", "");

        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        try {
            Solution router = new Solution();
            List<String> matches = new ArrayList<>();
            for (String clause : input.split(";")) {
                String[] parts = clause.trim().split("\\s+");
                if (parts.length == 3 && "reg".equals(parts[0])) {
                    router.register(parts[1], parts[2]);
                } else if (parts.length == 2 && "match".equals(parts[0])) {
                    matches.add(router.match(parts[1]));
                } else {
                    throw new IllegalArgumentException();
                }
            }
            if (matches.isEmpty()) {
                throw new IllegalArgumentException();
            }
            System.out.println("matches = " + matches);
        } catch (RuntimeException exception) {
            System.out.println("Could not read operations. " + USAGE);
        }
    }
}
