package com.jinloes.practice;

public class Solution {
    public static int evaluate(String expression) {
        int length = expression.length();
        int[] values = new int[length + 1];
        char[] operators = new char[length + 1];
        int valueTop = 0;
        int operatorTop = 0;
        boolean expectOperand = true;
        for (int i = 0; i < length; i++) {
            char ch = expression.charAt(i);
            if (ch == ' ') {
                continue;
            }
            if (ch >= '0' && ch <= '9') {
                int value = 0;
                while (i < length && expression.charAt(i) >= '0' && expression.charAt(i) <= '9') {
                    value = value * 10 + (expression.charAt(i) - '0');
                    i++;
                }
                i--;
                values[valueTop++] = value;
                expectOperand = false;
            } else if (ch == '(') {
                operators[operatorTop++] = '(';
                expectOperand = true;
            } else if (ch == ')') {
                while (operators[operatorTop - 1] != '(') {
                    valueTop = apply(values, valueTop, operators[--operatorTop]);
                }
                operatorTop--;
                expectOperand = false;
            } else {
                if (expectOperand) {
                    values[valueTop++] = 0;
                }
                while (operatorTop > 0 && operators[operatorTop - 1] != '('
                        && precedence(operators[operatorTop - 1]) >= precedence(ch)) {
                    valueTop = apply(values, valueTop, operators[--operatorTop]);
                }
                operators[operatorTop++] = ch;
                expectOperand = true;
            }
        }
        while (operatorTop > 0) {
            valueTop = apply(values, valueTop, operators[--operatorTop]);
        }
        return values[0];
    }

    private static int precedence(char operator) {
        return operator == '*' || operator == '/' ? 2 : 1;
    }

    private static int apply(int[] values, int top, char operator) {
        int right = values[--top];
        int left = values[--top];
        values[top++] = switch (operator) {
            case '+' -> left + right;
            case '-' -> left - right;
            case '*' -> left * right;
            default -> left / right;
        };
        return top;
    }
}
