package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: op1, op2, ... where ops are set k v | get k | delete k | begin | commit | rollback, for example: set a 1, begin, set a 2, get a, rollback, get a";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution s1 = new Solution();
        s1.set("a", "1");
        s1.begin();
        s1.set("a", "2");
        assertThat(s1.get("a")).as("Example 1 inner get").isEqualTo("2");
        assertThat(s1.rollback()).as("Example 1 rollback").isTrue();
        assertThat(s1.get("a")).as("Example 1 outer get").isEqualTo("1");

        Solution s2 = new Solution();
        s2.set("a", "1");
        s2.begin();
        s2.delete("a");
        assertThat(s2.get("a")).as("Example 2 deleted get").isNull();
        assertThat(s2.rollback()).as("Example 2 rollback").isTrue();
        assertThat(s2.get("a")).as("Example 2 restored get").isEqualTo("1");

        Solution s3 = new Solution();
        s3.begin();
        s3.set("x", "10");
        s3.begin();
        s3.set("x", "20");
        assertThat(s3.commit()).as("Example 3 inner commit").isTrue();
        assertThat(s3.get("x")).as("Example 3 outer get").isEqualTo("20");
        assertThat(s3.rollback()).as("Example 3 outer rollback").isTrue();
        assertThat(s3.get("x")).as("Example 3 base get").isNull();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty()) {
            System.out.println("Provide at least one operation. " + USAGE);
            return;
        }
        Solution store = new Solution();
        List<String> outputs = new ArrayList<>();
        for (String raw : input.split(",")) {
            String op = raw.trim();
            if (op.isEmpty()) {
                continue;
            }
            String[] parts = op.split("\\s+");
            switch (parts[0]) {
                case "set" -> {
                    if (parts.length != 3) {
                        System.out.println("Invalid set op. " + USAGE);
                        return;
                    }
                    store.set(parts[1], parts[2]);
                }
                case "get" -> {
                    if (parts.length != 2) {
                        System.out.println("Invalid get op. " + USAGE);
                        return;
                    }
                    outputs.add("get=" + store.get(parts[1]));
                }
                case "delete" -> {
                    if (parts.length != 2) {
                        System.out.println("Invalid delete op. " + USAGE);
                        return;
                    }
                    store.delete(parts[1]);
                }
                case "begin" -> {
                    if (parts.length != 1) {
                        System.out.println("Invalid begin op. " + USAGE);
                        return;
                    }
                    store.begin();
                }
                case "commit" -> {
                    if (parts.length != 1) {
                        System.out.println("Invalid commit op. " + USAGE);
                        return;
                    }
                    outputs.add("commit=" + store.commit());
                }
                case "rollback" -> {
                    if (parts.length != 1) {
                        System.out.println("Invalid rollback op. " + USAGE);
                        return;
                    }
                    outputs.add("rollback=" + store.rollback());
                }
                default -> {
                    System.out.println("Unknown op '" + parts[0] + "'. " + USAGE);
                    return;
                }
            }
        }
        System.out.println(String.join(", ", outputs));
    }
}
