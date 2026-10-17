package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;

public class Solution {
    private final Deque<Integer> stack = new ArrayDeque<>();
    public Solution() {}
    public void push(int value) { stack.push(value); }
    public int pop() { return stack.pop(); }
    public int top() { return stack.peek(); }
    public int peekMax() { return stack.stream().mapToInt(Integer::intValue).max().orElseThrow(); }
    public int popMax() { return stack.pop(); }
}
