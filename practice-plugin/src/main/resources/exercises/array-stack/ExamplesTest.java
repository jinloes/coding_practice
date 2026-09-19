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
        assertThat(stack.pop()).isEqualTo(9);
        assertThat(stack.pop()).isEqualTo(4);
    }

    @Test
    void peekDoesNotRemoveTheTop() {
        Solution stack = new Solution();
        stack.push(12);
        assertThat(stack.peek()).isEqualTo(12);
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void emptyOperationsThrow() {
        Solution stack = new Solution();
        assertThat(stack.isEmpty()).isTrue();
        assertThatThrownBy(stack::pop).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(stack::peek).isInstanceOf(NoSuchElementException.class);
    }
}
