package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    public static boolean isBalanced(String input) {
        Deque<Character> openings = new ArrayDeque<>();
        for (int index = 0; index < input.length(); index++) {
            char character = input.charAt(index);
            if (character == '(' || character == '[' || character == '{') {
                openings.push(character);
            } else {
                if (openings.isEmpty() || !matches(openings.pop(), character)) {
                    return false;
                }
            }
        }
        return openings.isEmpty();
    }

    private static boolean matches(char opening, char closing) {
        return (opening == '(' && closing == ')')
                || (opening == '[' && closing == ']')
                || (opening == '{' && closing == '}');
    }
}
