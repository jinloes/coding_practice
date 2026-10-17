package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: text, then removal characters separated by |, for example: abracadabra|ab";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args));
            return;
        }
        assertThat(Solution.removeSelected("Battle of the Vowels: Hawaii vs. Grozny", "aeiou"))
                .isEqualTo("Bttl f th Vwls: Hw vs. Grzny");
        assertThat(Solution.removeSelected("abracadabra", "ab")).isEqualTo("rcdr");
        assertThat(Solution.removeSelected("keep", "xyz")).isEqualTo("keep");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        int separator = input.indexOf('|');
        if (separator < 0 || input.indexOf('|', separator + 1) >= 0) {
            System.out.println("Expected exactly one | separator. " + USAGE);
            return;
        }
        String text = input.substring(0, separator);
        String remove = input.substring(separator + 1);
        System.out.println("removeSelected(\"" + text + "\", \"" + remove + "\") = \""
                + Solution.removeSelected(text, remove) + "\"");
    }
}
