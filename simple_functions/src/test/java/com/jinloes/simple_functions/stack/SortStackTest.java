package com.jinloes.simple_functions.stack;


import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;
import java.util.Stack;

import static org.assertj.core.api.Assertions.assertThat;


public class SortStackTest {
    private SortStack sortStack;

    @Before
    public void setUp() throws Exception {
        sortStack = new SortStack();
    }

    @Test
    public void sort() {

        Stack<Integer> stack = new Stack<>();
        stack.addAll(Lists.newArrayList(3, 1, 7, 2, 6, 9));

        sortStack.sort(stack);
        assertThat(stack).isSortedAccordingTo(Comparator.<Integer>naturalOrder().reversed());
    }
}