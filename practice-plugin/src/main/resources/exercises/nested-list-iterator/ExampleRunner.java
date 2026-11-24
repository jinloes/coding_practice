package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: nested integer list, for example: [[1,1],2,[1,1]]";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(drain(parse("[[1,1],2,[1,1]]"))).as("Example 1").containsExactly(1, 1, 2, 1, 1);
        assertThat(drain(parse("[1,[4,[6]]]"))).as("Example 2").containsExactly(1, 4, 6);
        assertThat(drain(parse("[[],[[]],[3,[]]]"))).as("Example 3").containsExactly(3);
        System.out.println("Examples passed: 3");
    }

    private static List<Integer> drain(List<Solution.NestedValue> items) {
        Solution iterator = new Solution(items);
        List<Integer> out = new ArrayList<>();
        while (iterator.hasNext()) {
            out.add(iterator.next());
        }
        return out;
    }

    private static void runInput(String input) {
        try {
            List<Solution.NestedValue> items = parse(input);
            System.out.println(drain(items));
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage() + " " + USAGE);
        }
    }

    private static List<Solution.NestedValue> parse(String input) {
        Parser parser = new Parser(input);
        Solution.NestedValue root = parser.parseList();
        parser.skipSpaces();
        if (!parser.eof()) {
            throw new IllegalArgumentException("Unexpected trailing characters.");
        }
        return root.asList();
    }

    private static final class Parser {
        private final String s;
        private int pos;

        Parser(String s) {
            this.s = s;
        }

        boolean eof() {
            return pos >= s.length();
        }

        void skipSpaces() {
            while (pos < s.length() && Character.isWhitespace(s.charAt(pos))) {
                pos++;
            }
        }

        Solution.NestedValue parseList() {
            skipSpaces();
            if (eof() || s.charAt(pos) != '[') {
                throw new IllegalArgumentException("Expected '['.");
            }
            pos++;
            List<Solution.NestedValue> items = new ArrayList<>();
            skipSpaces();
            if (!eof() && s.charAt(pos) == ']') {
                pos++;
                return Solution.NestedValue.ofList(items);
            }
            while (true) {
                skipSpaces();
                if (eof()) {
                    throw new IllegalArgumentException("Unterminated list.");
                }
                if (s.charAt(pos) == '[') {
                    items.add(parseList());
                } else {
                    items.add(parseInt());
                }
                skipSpaces();
                if (!eof() && s.charAt(pos) == ',') {
                    pos++;
                    continue;
                }
                if (!eof() && s.charAt(pos) == ']') {
                    pos++;
                    return Solution.NestedValue.ofList(items);
                }
                throw new IllegalArgumentException("Expected ',' or ']'.");
            }
        }

        Solution.NestedValue parseInt() {
            int start = pos;
            if (!eof() && s.charAt(pos) == '-') {
                pos++;
            }
            while (!eof() && Character.isDigit(s.charAt(pos))) {
                pos++;
            }
            if (pos == start || (pos == start + 1 && s.charAt(start) == '-')) {
                throw new IllegalArgumentException("Expected integer.");
            }
            return Solution.NestedValue.ofInt(Integer.parseInt(s.substring(start, pos)));
        }
    }
}
