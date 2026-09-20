package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void popsInLastInFirstOutOrder() {
        Solution stack = new Solution();
        stack.push(4);
        stack.push(9);
        assertThat(stack.pop()).as("first pop after pushing 4, 9").isEqualTo(9);
        assertThat(stack.pop()).as("second pop after pushing 4, 9").isEqualTo(4);
    }

    @Test
    void peekDoesNotRemoveTheTop() {
        Solution stack = new Solution();
        stack.push(12);
        assertThat(stack.peek()).as("peek after pushing 12").isEqualTo(12);
        assertThat(stack.size()).as("size after pushing 12 and peeking").isEqualTo(1);
    }

    @Test
    void emptyOperationsThrow() {
        Solution stack = new Solution();
        assertThat(stack.isEmpty()).as("a new stack must be empty").isTrue();
        assertThatThrownBy(stack::pop).as("pop() on an empty stack")
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(stack::peek).as("peek() on an empty stack")
                .isInstanceOf(NoSuchElementException.class);
    }
}
