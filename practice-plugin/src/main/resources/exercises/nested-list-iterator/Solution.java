package com.jinloes.practice;

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

        @Override
        public String toString() {
            return isInteger() ? String.valueOf(integer) : list.toString();
        }
    }

    public Solution(List<NestedValue> items) {
        throw new UnsupportedOperationException("Implement Nested List Iterator");
    }

    public boolean hasNext() {
        throw new UnsupportedOperationException("Implement hasNext");
    }

    public int next() {
        throw new UnsupportedOperationException("Implement next");
    }
}
