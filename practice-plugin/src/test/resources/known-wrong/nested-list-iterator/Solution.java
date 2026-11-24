package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Solution {
    public static final class NestedValue {
        private final Integer integer;
        private final List<NestedValue> list;

        private NestedValue(Integer integer, List<NestedValue> list) {
            this.integer = integer;
            this.list = list;
        }

        public static NestedValue ofInt(int value) {
            return new NestedValue(value, null);
        }

        public static NestedValue ofList(List<NestedValue> items) {
            return new NestedValue(null, List.copyOf(items));
        }

        public boolean isInteger() {
            return integer != null;
        }

        public int asInteger() {
            if (integer == null) {
                throw new IllegalStateException("Not an integer");
            }
            return integer;
        }

        public List<NestedValue> asList() {
            if (list == null) {
                throw new IllegalStateException("Not a list");
            }
            return list;
        }
    }

    private final Deque<Iterator<NestedValue>> stack = new ArrayDeque<>();

    public Solution(List<NestedValue> items) {
        if (!items.isEmpty()) {
            stack.push(items.iterator());
        }
    }

    public boolean hasNext() {
        // Bug: only checks whether the stack is non-empty, failing on empty nested lists.
        return !stack.isEmpty() && stack.peek().hasNext();
    }

    public int next() {
        while (!stack.isEmpty()) {
            Iterator<NestedValue> top = stack.peek();
            if (!top.hasNext()) {
                stack.pop();
                continue;
            }
            NestedValue candidate = top.next();
            if (candidate.isInteger()) {
                return candidate.asInteger();
            }
            stack.push(candidate.asList().iterator());
        }
        throw new NoSuchElementException("No more elements");
    }
}
