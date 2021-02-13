package com.jinloes.simple_functions.stack;

import java.util.Stack;

/**
 * Sort a stack only using a temporary stack to sort.
 */
public class SortStack {

    public void sort(Stack<Integer> toSort) {
        Stack<Integer> temp = new Stack<>();

        while (!toSort.isEmpty()) {
            if (temp.isEmpty() || temp.peek() > toSort.peek()) {
                temp.push(toSort.pop());
            } else {
                int current = toSort.pop();
                while (!temp.isEmpty() && temp.peek() < current) {
                    toSort.push(temp.pop());
                }
                temp.push(current);
            }
        }
        toSort.addAll(temp);
    }
}
