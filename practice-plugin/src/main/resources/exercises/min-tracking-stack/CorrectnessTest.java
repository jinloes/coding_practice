package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void singleValueIsItsOwnMinimum() {
        Solution stack = new Solution();
        stack.push(-3);
        assertThat(stack.min()).as("min").isEqualTo(-3);
        assertThat(stack.top()).as("top").isEqualTo(-3);
        assertThat(stack.size()).as("size").isEqualTo(1);
    }

    @Test
    void duplicateMinimumsAreCountedSeparately() {
        Solution stack = new Solution();
        stack.push(4);
        stack.push(1);
        stack.push(1);
        assertThat(stack.pop()).as("pop of the second 1").isEqualTo(1);
        assertThat(stack.min()).as("min while one 1 remains").isEqualTo(1);
        assertThat(stack.pop()).as("pop of the first 1").isEqualTo(1);
        assertThat(stack.min()).as("min once both 1s are gone").isEqualTo(4);
    }

    @Test
    void minimumTracksAFallingThenRisingSequence() {
        Solution stack = new Solution();
        int[] pushes = {9, 6, 8, 3, 5, 3, 10};
        int[] minimums = {9, 6, 6, 3, 3, 3, 3};
        for (int i = 0; i < pushes.length; i++) {
            stack.push(pushes[i]);
            assertThat(stack.min()).as("min after pushing %d", pushes[i]).isEqualTo(minimums[i]);
        }
        for (int i = pushes.length - 1; i > 0; i--) {
            assertThat(stack.pop()).as("pop #%d", i).isEqualTo(pushes[i]);
            assertThat(stack.min()).as("min after pop #%d", i).isEqualTo(minimums[i - 1]);
        }
    }

    @Test
    void handlesExtremeValues() {
        Solution stack = new Solution();
        stack.push(Integer.MAX_VALUE);
        stack.push(Integer.MIN_VALUE);
        stack.push(0);
        assertThat(stack.min()).as("min with MIN_VALUE stored").isEqualTo(Integer.MIN_VALUE);
        stack.pop();
        stack.pop();
        assertThat(stack.min()).as("min after MIN_VALUE popped").isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void topAndMinDoNotRemoveValues() {
        Solution stack = new Solution();
        stack.push(2);
        stack.push(8);
        stack.top();
        stack.min();
        assertThat(stack.size()).as("size after top and min").isEqualTo(2);
    }

    @Test
    void becomesEmptyAgainAfterPoppingEverything() {
        Solution stack = new Solution();
        stack.push(1);
        stack.pop();
        assertThat(stack.size()).as("size after popping the only value").isZero();
        assertThatThrownBy(stack::min).as("min after emptying").isInstanceOf(NoSuchElementException.class);
        stack.push(7);
        assertThat(stack.min()).as("min after reuse").isEqualTo(7);
    }

    @Test
    void growsPastAnyInitialCapacity() {
        Solution stack = new Solution();
        for (int i = 20_000; i > 0; i--) {
            stack.push(i);
        }
        assertThat(stack.min()).as("min after 20000 descending pushes").isEqualTo(1);
        assertThat(stack.size()).as("size").isEqualTo(20_000);
        for (int i = 1; i <= 19_999; i++) {
            stack.pop();
        }
        assertThat(stack.min()).as("min with one value left").isEqualTo(20_000);
    }
}
