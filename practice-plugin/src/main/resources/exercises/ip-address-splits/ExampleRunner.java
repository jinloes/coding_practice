package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one digit string, for example: 25525511135";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join("", args).trim());
            return;
        }
        assertThat(Solution.splitIpAddresses("25525511135"))
                .containsExactly("255.255.11.135", "255.255.111.35");
        assertThat(Solution.splitIpAddresses("0000")).containsExactly("0.0.0.0");
        assertThat(Solution.splitIpAddresses("123")).isEmpty();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.isEmpty() || !input.chars().allMatch(Character::isDigit)) {
            System.out.println("Expected decimal digits only. " + USAGE);
            return;
        }
        System.out.println("splitIpAddresses(\"" + input + "\") = "
                + Solution.splitIpAddresses(input));
    }
}
