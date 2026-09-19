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
        assertThat(stack.isEmpty()).isTrue();
        assertThat(stack.size()).isZero();
    }

    @Test
    void followsAnOracleForMixedOperations() {
        Solution stack = new Solution();
        Deque<Integer> oracle = new ArrayDeque<>();
        for (int i = 0; i < 40; i++) {
            stack.push(i - 20);
            oracle.push(i - 20);
            if (i % 3 == 2) {
                assertThat(stack.pop()).isEqualTo(oracle.pop());
            }
            assertThat(stack.size()).isEqualTo(oracle.size());
        }
        while (!oracle.isEmpty()) {
            assertThat(stack.pop()).isEqualTo(oracle.pop());
        }
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void acceptsDuplicatesAndNegativeValues() {
        Solution stack = new Solution();
        stack.push(-1);
        stack.push(-1);
        stack.push(Integer.MAX_VALUE);
        assertThat(stack.pop()).isEqualTo(Integer.MAX_VALUE);
        assertThat(stack.pop()).isEqualTo(-1);
        assertThat(stack.pop()).isEqualTo(-1);
    }

    @Test
    void growsBeyondAnInitialCapacity() {
        Solution stack = new Solution();
        for (int i = 0; i < 512; i++) {
            stack.push(i);
        }
        assertThat(stack.size()).isEqualTo(512);
        for (int i = 511; i >= 0; i--) {
            assertThat(stack.pop()).isEqualTo(i);
        }
        assertThat(stack.isEmpty()).isTrue();
    }

    @Test
    void peekAndSizeRemainStableUntilPop() {
        Solution stack = new Solution();
        stack.push(3);
        stack.push(7);
        assertThat(stack.peek()).isEqualTo(7);
        assertThat(stack.peek()).isEqualTo(7);
        assertThat(stack.size()).isEqualTo(2);
        assertThat(stack.pop()).isEqualTo(7);
        assertThat(stack.peek()).isEqualTo(3);
        assertThat(stack.size()).isEqualTo(1);
    }

    @Test
    void emptyPopThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().pop())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void emptyPeekThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().peek())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void sizeAndEmptyTrackEveryRemoval() {
        Solution stack = new Solution();
        for (int i = 1; i <= 8; i++) {
            stack.push(i);
            assertThat(stack.isEmpty()).isFalse();
            assertThat(stack.size()).isEqualTo(i);
        }
        for (int i = 8; i >= 1; i--) {
            assertThat(stack.pop()).isEqualTo(i);
            assertThat(stack.size()).isEqualTo(i - 1);
        }
        assertThat(stack.isEmpty()).isTrue();
    }
}
