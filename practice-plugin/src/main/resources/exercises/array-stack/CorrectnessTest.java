package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void startsEmpty() {
        Solution stack = new Solution();
        assertThat(stack.isEmpty()).as("a new stack must be empty").isTrue();
        assertThat(stack.size()).as("a new stack must have size 0").isZero();
    }

    @Test
    void followsAnOracleForMixedOperations() {
        Solution stack = new Solution();
        Deque<Integer> oracle = new ArrayDeque<>();
        for (int i = 0; i < 40; i++) {
            stack.push(i - 20);
            oracle.push(i - 20);
            if (i % 3 == 2) {
                assertThat(stack.pop()).as("pop after pushing %d values", i + 1).isEqualTo(oracle.pop());
            }
            assertThat(stack.size()).as("size after %d pushes and the interleaved pops", i + 1)
                    .isEqualTo(oracle.size());
        }
        while (!oracle.isEmpty()) {
            assertThat(stack.pop()).as("pop with %d values still expected", oracle.size())
                    .isEqualTo(oracle.pop());
        }
        assertThat(stack.isEmpty()).as("the stack must be empty after draining it").isTrue();
    }

    @Test
    void acceptsDuplicatesAndNegativeValues() {
        Solution stack = new Solution();
        stack.push(-1);
        stack.push(-1);
        stack.push(Integer.MAX_VALUE);
        assertThat(stack.pop()).as("first pop after pushing -1, -1, MAX_VALUE").isEqualTo(Integer.MAX_VALUE);
        assertThat(stack.pop()).as("second pop after pushing -1, -1, MAX_VALUE").isEqualTo(-1);
        assertThat(stack.pop()).as("third pop after pushing -1, -1, MAX_VALUE").isEqualTo(-1);
    }

    @Test
    void growsBeyondAnInitialCapacity() {
        Solution stack = new Solution();
        for (int i = 0; i < 512; i++) {
            stack.push(i);
        }
        assertThat(stack.size()).as("size after pushing 0..511").isEqualTo(512);
        for (int i = 511; i >= 0; i--) {
            assertThat(stack.pop()).as("pop number %d after pushing 0..511", 512 - i).isEqualTo(i);
        }
        assertThat(stack.isEmpty()).as("the stack must be empty after popping all 512 values").isTrue();
    }

    @Test
    void peekAndSizeRemainStableUntilPop() {
        Solution stack = new Solution();
        stack.push(3);
        stack.push(7);
        assertThat(stack.peek()).as("first peek after pushing 3, 7").isEqualTo(7);
        assertThat(stack.peek()).as("second peek after pushing 3, 7 must return the same value").isEqualTo(7);
        assertThat(stack.size()).as("size after pushing 3, 7 and peeking twice").isEqualTo(2);
        assertThat(stack.pop()).as("pop after pushing 3, 7").isEqualTo(7);
        assertThat(stack.peek()).as("peek after pushing 3, 7 and popping once").isEqualTo(3);
        assertThat(stack.size()).as("size after pushing 3, 7 and popping once").isEqualTo(1);
    }

    @Test
    void emptyPopThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().pop())
                .as("pop() on an empty stack").isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void emptyPeekThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().peek())
                .as("peek() on an empty stack").isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void sizeAndEmptyTrackEveryRemoval() {
        Solution stack = new Solution();
        for (int i = 1; i <= 8; i++) {
            stack.push(i);
            assertThat(stack.isEmpty()).as("the stack must not be empty after pushing 1..%d", i).isFalse();
            assertThat(stack.size()).as("size after pushing 1..%d", i).isEqualTo(i);
        }
        for (int i = 8; i >= 1; i--) {
            assertThat(stack.pop()).as("pop with values 1..%d remaining", i).isEqualTo(i);
            assertThat(stack.size()).as("size after popping %d", i).isEqualTo(i - 1);
        }
        assertThat(stack.isEmpty()).as("the stack must be empty after popping all 8 values").isTrue();
    }
}
