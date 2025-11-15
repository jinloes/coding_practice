package com.jinloes.graphs;

// java
import java.util.ArrayList;
import java.util.List;

public class PhoneLetterCombinations {
    private static final String[] MAPPING = {
        "",    // 0
        "",    // 1
        "abc", // 2
        "def", // 3
        "ghi", // 4
        "jkl", // 5
        "mno", // 6
        "pqrs",// 7
        "tuv", // 8
        "wxyz" // 9
    };

    public static List<String> letterCombinations(String digits) {
        List<String> results = new ArrayList<>();
        if (digits == null || digits.isEmpty()) {
            return results;
        }
        backtrack(digits, 0, new StringBuilder(), results);
        return results;
    }

    private static void backtrack(String digits, int index, StringBuilder current, List<String> results) {
        if (index == digits.length()) {
            results.add(current.toString());
            return;
        }
        char d = digits.charAt(index);
        if (d < '0' || d > '9') {
            return;
        }
        String letters = MAPPING[d - '0'];
        if (letters.isEmpty()) {
            backtrack(digits, index + 1, current, results);
            return;
        }
        for (char c : letters.toCharArray()) {
            current.append(c);
            backtrack(digits, index + 1, current, results);
            current.deleteCharAt(current.length() - 1);
        }
    }

    public static void printCombinations(String digits) {
        for (String combo : letterCombinations(digits)) {
            System.out.println(combo);
        }
    }

    public static void main(String[] args) {
        String digits = args.length > 0 ? args[0] : "23";
        printCombinations(digits);
    }
}
