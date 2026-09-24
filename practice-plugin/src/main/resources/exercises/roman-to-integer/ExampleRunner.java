package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: one Roman numeral from I to MMMCMXCIX, for example: MCMXCIV";
    private static final String STANDARD_NUMERAL =
            "M{0,3}(CM|CD|D?C{0,3})(XC|XL|L?X{0,3})(IX|IV|V?I{0,3})";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.romanToInt("III")).as("Example 1: romanToInt(\"III\")").isEqualTo(3);
        assertThat(Solution.romanToInt("LVIII")).as("Example 2: romanToInt(\"LVIII\")").isEqualTo(58);
        assertThat(Solution.romanToInt("MCMXCIV")).as("Example 3: romanToInt(\"MCMXCIV\")").isEqualTo(1994);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String numeral = input.replace('"', ' ').trim();
        if (numeral.isEmpty() || !numeral.matches(STANDARD_NUMERAL)) {
            System.out.println("\"" + numeral + "\" is not a standard uppercase Roman numeral. " + USAGE);
            return;
        }
        System.out.println("romanToInt(\"" + numeral + "\") = " + Solution.romanToInt(numeral));
    }
}
