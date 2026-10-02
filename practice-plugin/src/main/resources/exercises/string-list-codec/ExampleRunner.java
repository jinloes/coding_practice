package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: the strings joined by | inside square brackets, for example: [hello||a,b|w#rld]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertRoundTrip(List.of("hello", "world"));
        assertRoundTrip(List.of(""));
        assertRoundTrip(List.of("a,b", "c"));
        System.out.println("Examples passed: 3");
    }

    private static void assertRoundTrip(List<String> original) {
        String encoded = Solution.encode(new ArrayList<>(original));
        assertThat(encoded).as("encode(%s) must not be null", original).isNotNull();
        assertThat(Solution.decode(encoded))
                .as("decode(encode(%s)) with encoded form \"%s\"", original, encoded)
                .isEqualTo(original);
    }

    private static void runInput(String input) {
        List<String> strings = parse(input);
        if (strings == null) {
            System.out.println("Could not read \"" + input + "\" as a list. " + USAGE);
            return;
        }
        String encoded = Solution.encode(new ArrayList<>(strings));
        List<String> decoded = Solution.decode(encoded);
        System.out.println("encode(" + strings + ") = \"" + encoded + "\"");
        System.out.println("decode(...) = " + decoded + (decoded.equals(strings) ? " (round trip ok)"
                : " (does not match the original)"));
    }

    private static List<String> parse(String input) {
        if (!input.startsWith("[") || !input.endsWith("]")) {
            return null;
        }
        String body = input.substring(1, input.length() - 1);
        if (body.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(List.of(body.split("\\|", -1)));
    }
}
