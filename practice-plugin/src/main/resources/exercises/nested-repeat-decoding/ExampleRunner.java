package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: an encoded string of lowercase letters, counts, and brackets, for example: 2[x3[y]]z";
    private static final long MAX_DECODED_LENGTH = 100_000;

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        assertThat(Solution.decode("3[a]2[bc]")).as("Example 1: 3[a]2[bc]").isEqualTo("aaabcbc");
        assertThat(Solution.decode("2[x3[y]]z")).as("Example 2: 2[x3[y]]z").isEqualTo("xyyyxyyyz");
        assertThat(Solution.decode("10[k]")).as("Example 3: 10[k]").isEqualTo("kkkkkkkkkk");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        if (!wellFormed(input)) {
            System.out.println("Could not read \"" + input + "\" as an encoded string. " + USAGE);
            return;
        }
        System.out.println("decode(\"" + input + "\") = \"" + Solution.decode(input) + "\"");
    }

    /** Checks the grammar and that the decoded length stays within the contract, without decoding. */
    private static boolean wellFormed(String input) {
        Deque<long[]> open = new ArrayDeque<>();
        long length = 0;
        int i = 0;
        while (i < input.length()) {
            char ch = input.charAt(i);
            if (ch >= 'a' && ch <= 'z') {
                length++;
                i++;
            } else if (ch >= '1' && ch <= '9') {
                int end = i;
                while (end < input.length() && Character.isDigit(input.charAt(end))) {
                    end++;
                }
                if (end - i > 3 || end == input.length() || input.charAt(end) != '[') {
                    return false;
                }
                int count = Integer.parseInt(input.substring(i, end));
                if (count > 300) {
                    return false;
                }
                open.push(new long[]{count, length});
                length = 0;
                i = end + 1;
            } else if (ch == ']' && !open.isEmpty()) {
                long[] group = open.pop();
                length = group[1] + group[0] * length;
                i++;
            } else {
                return false;
            }
            if (length > MAX_DECODED_LENGTH) {
                return false;
            }
        }
        return open.isEmpty();
    }
}
