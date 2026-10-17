package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyAndSingleElementStacks() {
        Stack<Integer> empty = new Stack<>();
        Solution.sort(empty);
        assertThat(empty).isEmpty();
        Stack<Integer> single = stackOf(9);
        Solution.sort(single);
        assertThat(single).containsExactly(9);
    }

    @Test
    void sortsManySeededValues() {
        Stack<Integer> stack = new Stack<>();
        Random random = new Random(11);
        for (int i = 0; i < 200; i++) stack.push(random.nextInt(41) - 20);
        Solution.sort(stack);
        for (int i = 1; i < stack.size(); i++) {
            assertThat(stack.get(i - 1)).as("descending at %s", i).isGreaterThanOrEqualTo(stack.get(i));
        }
    }

    private static Stack<Integer> stackOf(int... values) {
        Stack<Integer> stack = new Stack<>();
        for (int value : values) stack.push(value);
        return stack;
    }
}
