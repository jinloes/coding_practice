package com.jinloes.simple_functions;

import java.util.Stack;

class ValidParens {
    public boolean isValid(String s) {
        Stack<Character> openParenStack = new Stack<>();

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (isOpenParen(c)) {
                openParenStack.push(c);
            }
            if (isClosedParen(c)) {
                if (openParenStack.isEmpty()) {
                    return false;
                }
                if (openParenStack.peek() == getOpenParen(c)) {
                    openParenStack.pop();
                } else {
                    return false;
                }
            }

        }
        return openParenStack.empty();
    }

    private boolean isOpenParen(char c) {
        return c == '{' || c == '[' || c == '(';
    }

    private boolean isClosedParen(char c) {
        return c == '}' || c == ']' || c == ')';
    }

    private char getOpenParen(char closedParen) {
        switch (closedParen) {
            case ']':
                return '[';
            case '}':
                return '{';
            case ')':
                return '(';
            default:
                throw new IllegalArgumentException("Character not supported:" + closedParen);
        }
    }
}
