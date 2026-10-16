package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void reportsMinimumAndTop() {
        Solution stack = new Solution();
        stack.push(5);
        stack.push(2);
        stack.push(7);
        assertThat(stack.min()).as("min after pushing 5, 2, 7").isEqualTo(2);
        assertThat(stack.top()).as("top after pushing 5, 2, 7").isEqualTo(7);
    }

    @Test
    void minimumReturnsToAnEarlierValueAfterPops() {
        Solution stack = new Solution();
        stack.push(5);
        stack.push(2);
        stack.push(7);
        assertThat(stack.pop()).as("first pop").isEqualTo(7);
        assertThat(stack.pop()).as("second pop").isEqualTo(2);
        assertThat(stack.min()).as("min after popping 7 and 2").isEqualTo(5);
    }

    @Test
    void emptyStackThrows() {
        Solution stack = new Solution();
        assertThatThrownBy(stack::min).as("min on empty stack").isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(stack::top).as("top on empty stack").isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(stack::pop).as("pop on empty stack").isInstanceOf(NoSuchElementException.class);
    }
}
