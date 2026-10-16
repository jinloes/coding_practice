package com.jinloes.practice;

public class Solution {
    public static int evaluate(String[] tokens) {
        int[] stack = new int[tokens.length];
        int top = 0;
        for (String token : tokens) {
            if (token.length() == 1 && "+-*/".indexOf(token.charAt(0)) >= 0) {
                int right = stack[--top];
                int left = stack[--top];
                stack[top++] = switch (token.charAt(0)) {
                    case '+' -> left + right;
                    case '-' -> left - right;
                    case '*' -> left * right;
                    default -> left / right;
                };
            } else {
                stack[top++] = Integer.parseInt(token);
            }
        }
        return stack[0];
    }
}
