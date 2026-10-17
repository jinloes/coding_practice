package com.jinloes.practice;

import java.util.Stack;

public class Solution {
    public static void sort(Stack<Integer> stack) {
        Stack<Integer> helper = new Stack<>();
        while (!stack.isEmpty()) {
            Integer value = stack.pop();
            while (!helper.isEmpty() && helper.peek() > value) {
                stack.push(helper.pop());
            }
            helper.push(value);
        }
        while (!helper.isEmpty()) {
            stack.push(helper.pop());
        }
    }
}


