package com.jinloes.coding_practice;

import java.util.Comparator;
import java.util.TreeSet;

class MaxStack2 {
    private TreeSet<int[]> stack;
    private TreeSet<int[]> values;
    private int counter;

    public MaxStack2() {
        stack = new TreeSet<>(Comparator.comparing(arr -> arr[0]));
        values = new TreeSet<>(Comparator.<int[]>comparingInt(arr -> arr[1])
                .reversed()
                .thenComparing(Comparator.<int[]>comparingInt(arr -> arr[0]).reversed()));
        counter = 0;

    }

    public void push(int x) {
        int[] record = new int[]{counter, x};
        counter++;
        stack.add(record);
        values.add(record);
    }

    public int pop() {
        int[] record = stack.pollLast();
        values.remove(record);
        return record[1];
    }

    public int top() {
        return stack.last()[1];

    }

    public int peekMax() {
        return values.first()[1];

    }

    public int popMax() {
        int[] record = values.pollFirst();
        stack.remove(record);
        return record[1];
    }
}
