package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void removesMostRecentMaximum() {
        Solution stack = new Solution();
        stack.push(5);
        stack.push(1);
        stack.push(5);
        assertThat(stack.top()).as("top").isEqualTo(5);
        assertThat(stack.popMax()).as("popMax").isEqualTo(5);
        assertThat(stack.top()).as("top after popMax").isEqualTo(1);
        assertThat(stack.peekMax()).as("peekMax").isEqualTo(5);
        assertThat(stack.pop()).as("pop").isEqualTo(1);
    }

    @Test
    void handlesDuplicateNegativeAndPositiveValues() {
        Solution stack = new Solution();
        stack.push(-2);
        stack.push(7);
        stack.push(7);
        assertThat(stack.popMax()).as("newer max first").isEqualTo(7);
        assertThat(stack.popMax()).as("older max second").isEqualTo(7);
        assertThat(stack.pop()).as("last value").isEqualTo(-2);
    }
}
