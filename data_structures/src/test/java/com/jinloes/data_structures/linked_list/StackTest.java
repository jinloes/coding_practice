package com.jinloes.data_structures.linked_list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StackTest {
    private Stack<Integer> stack;

    @BeforeEach
    public void setUp() throws Exception {
        stack = new Stack<>();
    }

    @Test
    public void push() {
        stack.push(1);

        assertThat(stack.peek()).isEqualTo(1);

        stack.push(2);

        assertThat(stack.peek()).isEqualTo(2);

        stack.push(3);
        stack.push(4);

        assertThat(stack.peek()).isEqualTo(4);
    }

    @Test
    public void pop() {
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);

        assertThat(stack.pop()).isEqualTo(4);
        assertThat(stack.peek()).isEqualTo(3);

        assertThat(stack.pop()).isEqualTo(3);
        assertThat(stack.pop()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(1);
        assertThat(stack.pop()).isNull();

        stack.push(4);
        assertThat(stack.peek()).isEqualTo(4);
    }
}