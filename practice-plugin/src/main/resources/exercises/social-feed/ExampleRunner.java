package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by semicolons, each post <user> <postId>, follow <a> <b>, unfollow <a> <b> or feed <user>, for example: post 1 5; post 2 6; follow 1 2; feed 1";

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
        Solution feed = new Solution();
        feed.post(1, 5);
        feed.post(2, 6);
        feed.follow(1, 2);
        assertThat(feed.feed(1)).as("Example 1: feed(1)").containsExactly(6, 5);
        assertThat(feed.feed(2)).as("Example 1: feed(2)").containsExactly(6);
    }

    private static void example2() {
        Solution feed = new Solution();
        feed.follow(1, 2);
        feed.post(2, 8);
        feed.post(1, 9);
        feed.unfollow(1, 2);
        assertThat(feed.feed(1)).as("Example 2: feed(1)").containsExactly(9);
    }

    private static void example3() {
        Solution feed = new Solution();
        for (int id = 1; id <= 12; id++) {
            feed.post(3, id);
        }
        assertThat(feed.feed(3)).as("Example 3: feed(3)").containsExactly(12, 11, 10, 9, 8, 7, 6, 5, 4, 3);
    }

    private static void runInput(String input) {
        Solution feed = new Solution();
        StringBuilder output = new StringBuilder();
        try {
            for (String raw : input.split(";")) {
                String operation = raw.trim();
                if (operation.isEmpty()) {
                    continue;
                }
                String[] parts = operation.split("\\s+");
                switch (parts[0]) {
                    case "post" -> feed.post(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    case "follow" -> feed.follow(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    case "unfollow" -> feed.unfollow(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    case "feed" -> {
                        List<Integer> posts = feed.feed(Integer.parseInt(parts[1]));
                        output.append("feed(").append(parts[1]).append(") = ").append(posts).append('\n');
                    }
                    default -> {
                        System.out.println("Unknown operation '" + parts[0] + "'. " + USAGE);
                        return;
                    }
                }
            }
        } catch (IllegalArgumentException | IndexOutOfBoundsException exception) {
            System.out.println("Could not read \"" + input + "\". " + USAGE);
            return;
        }
        System.out.println(output.length() == 0 ? "No feed requested. " + USAGE : output.toString().trim());
    }
}
