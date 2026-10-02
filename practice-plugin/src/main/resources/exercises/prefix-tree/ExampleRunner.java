package com.jinloes.practice;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: insert apple, contains app, hasPrefix app, contains apple";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        Solution trie = new Solution();
        trie.insert("apple");
        assertThat(trie.contains("apple")).as("contains apple").isTrue();
        assertThat(trie.contains("app")).as("contains app").isFalse();
        assertThat(trie.hasPrefix("app")).as("hasPrefix app").isTrue();
    }

    private static void example2() {
        Solution trie = new Solution();
        trie.insert("car");
        trie.insert("cart");
        assertThat(trie.contains("car")).isTrue();
        assertThat(trie.contains("cart")).isTrue();
        assertThat(trie.contains("carts")).isFalse();
    }

    private static void example3() {
        Solution trie = new Solution();
        assertThat(trie.contains("a")).isFalse();
        assertThat(trie.hasPrefix("a")).isFalse();
    }

    private static void runInput(String input) {
        Solution trie = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            String[] parts = operation.split("\\s+");
            if (parts.length != 2 || !parts[1].matches("[a-z]+")) {
                System.out.println("'" + operation + "' needs an operation and a lowercase word. " + USAGE);
                return;
            }
            switch (parts[0]) {
                case "insert" -> {
                    trie.insert(parts[1]);
                    System.out.println("insert(" + parts[1] + ")");
                }
                case "contains" -> System.out.println("contains(" + parts[1] + ") = " + trie.contains(parts[1]));
                case "hasPrefix" -> System.out.println("hasPrefix(" + parts[1] + ") = " + trie.hasPrefix(parts[1]));
                default -> {
                    System.out.println("Unknown operation '" + parts[0] + "'. " + USAGE);
                    return;
                }
            }
        }
    }


}
