package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: len N; op1, op2, ... where ops are set i v | snap | get i s, for example: len 3; set 0 5, snap, set 0 6, get 0 0";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution a1 = new Solution(3);
        a1.set(0, 5);
        assertThat(a1.snap()).as("Example 1 snap").isEqualTo(0);
        a1.set(0, 6);
        assertThat(a1.get(0, 0)).as("Example 1 get").isEqualTo(5);

        Solution a2 = new Solution(3);
        a2.set(1, 2);
        a2.set(1, 9);
        assertThat(a2.snap()).as("Example 2 snap").isEqualTo(0);
        assertThat(a2.get(1, 0)).as("Example 2 get").isEqualTo(9);

        Solution a3 = new Solution(3);
        assertThat(a3.snap()).as("Example 3 snap").isEqualTo(0);
        assertThat(a3.get(2, 0)).as("Example 3 get").isEqualTo(0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'len N; ops'. " + USAGE);
            return;
        }
        String[] header = sections[0].trim().split("\\s+");
        if (header.length != 2 || !"len".equals(header[0])) {
            System.out.println("Start with 'len N'. " + USAGE);
            return;
        }
        try {
            int length = Integer.parseInt(header[1]);
            if (length < 1) {
                System.out.println("Length must be at least 1. " + USAGE);
                return;
            }
            Solution arr = new Solution(length);
            List<String> outputs = new ArrayList<>();
            for (String raw : sections[1].split(",")) {
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
                        arr.set(Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
                    }
                    case "snap" -> {
                        if (parts.length != 1) {
                            System.out.println("Invalid snap op. " + USAGE);
                            return;
                        }
                        outputs.add("snap=" + arr.snap());
                    }
                    case "get" -> {
                        if (parts.length != 3) {
                            System.out.println("Invalid get op. " + USAGE);
                            return;
                        }
                        outputs.add("get=" + arr.get(Integer.parseInt(parts[1]), Integer.parseInt(parts[2])));
                    }
                    default -> {
                        System.out.println("Unknown op '" + parts[0] + "'. " + USAGE);
                        return;
                    }
                }
            }
            System.out.println(String.join(", ", outputs));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer argument. " + USAGE);
        }
    }
}
