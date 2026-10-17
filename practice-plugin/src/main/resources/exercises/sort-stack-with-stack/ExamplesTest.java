package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void sortsDistinctValuesDescendingFromBottom() {
        Stack<Integer> stack = stackOf(3, 1, 2);
        Solution.sort(stack);
        assertThat(stack).containsExactly(3, 2, 1);
    }

    @Test
    void keepsDuplicatesAndNegatives() {
        Stack<Integer> stack = stackOf(4, 4, -1);
        Solution.sort(stack);
        assertThat(stack).containsExactly(4, 4, -1);
    }

    private static Stack<Integer> stackOf(int... values) {
        Stack<Integer> stack = new Stack<>();
        for (int value : values) stack.push(value);
        return stack;
    }
}
