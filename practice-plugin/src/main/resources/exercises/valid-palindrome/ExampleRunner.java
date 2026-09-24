package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the text between single quotes, for example: 'Was it a car or a cat I saw?'";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.isPalindrome("Was it a car or a cat I saw?"))
                .as("Example 1: isPalindrome(\"Was it a car or a cat I saw?\")").isTrue();
        assertThat(Solution.isPalindrome("race a car"))
                .as("Example 2: isPalindrome(\"race a car\")").isFalse();
        assertThat(Solution.isPalindrome(".,!"))
                .as("Example 3: isPalindrome(\".,!\")").isTrue();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (input.length() < 2 || input.charAt(0) != '\'' || input.charAt(input.length() - 1) != '\'') {
            System.out.println("Put the text between single quotes so its start and end are unambiguous. "
                    + USAGE);
            return;
        }
        String text = input.substring(1, input.length() - 1);
        if (!text.chars().allMatch(character -> character >= 32 && character <= 126)) {
            System.out.println("The contract allows only printable ASCII characters. " + USAGE);
            return;
        }
        System.out.println("isPalindrome(\"" + text + "\") = " + Solution.isPalindrome(text));
    }
}
