package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void startsEmpty() {
        Solution heap = new Solution();
        assertThat(heap.isEmpty()).as("a new heap must be empty").isTrue();
        assertThat(heap.size()).as("a new heap must have size 0").isZero();
    }

    @Test
    void handlesSingletonAndBoundaryValues() {
        Solution heap = new Solution();
        heap.add(Integer.MAX_VALUE);
        heap.add(Integer.MIN_VALUE);
        assertThat(heap.peek()).as("peek after adding MAX_VALUE, MIN_VALUE").isEqualTo(Integer.MIN_VALUE);
        assertThat(heap.removeMin()).as("first removeMin after adding MAX_VALUE, MIN_VALUE").isEqualTo(Integer.MIN_VALUE);
        assertThat(heap.removeMin()).as("second removeMin after adding MAX_VALUE, MIN_VALUE").isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    void followsPriorityQueueForMixedOperations() {
        Solution heap = new Solution();
        Queue<Integer> oracle = new PriorityQueue<>();
        for (int i = 0; i < 100; i++) {
            int value = (i * 37) % 101 - 50;
            heap.add(value);
            oracle.add(value);
            if (i % 4 == 3) {
                assertThat(heap.removeMin()).as("removeMin after adding %d values, last was %d", i + 1, value)
                        .isEqualTo(oracle.remove());
            }
            assertThat(heap.size()).as("size after adding %d values, last was %d", i + 1, value)
                    .isEqualTo(oracle.size());
            assertThat(heap.peek()).as("peek after adding %d values, last was %d", i + 1, value)
                    .isEqualTo(oracle.peek());
        }
        while (!oracle.isEmpty()) {
            assertThat(heap.removeMin()).as("removeMin with %d values still expected", oracle.size())
                    .isEqualTo(oracle.remove());
        }
        assertThat(heap.isEmpty()).as("the heap must be empty after draining it").isTrue();
    }

    @Test
    void growsBeyondAnInitialCapacity() {
        Solution heap = new Solution();
        Queue<Integer> oracle = new PriorityQueue<>();
        for (int i = 0; i < 512; i++) {
            int value = 512 - i;
            heap.add(value);
            oracle.add(value);
        }
        assertThat(heap.size()).as("size after adding 512 descending values").isEqualTo(512);
        while (!oracle.isEmpty()) {
            assertThat(heap.removeMin())
                    .as("removeMin with %d of the 512 values still expected", oracle.size())
                    .isEqualTo(oracle.remove());
        }
    }

    @Test
    void handlesDuplicatesAndNegativeValues() {
        Solution heap = new Solution();
        heap.add(-3);
        heap.add(4);
        heap.add(-3);
        heap.add(0);
        assertThat(heap.removeMin()).as("removeMin 1 of 4 after adding -3, 4, -3, 0").isEqualTo(-3);
        assertThat(heap.removeMin()).as("removeMin 2 of 4 after adding -3, 4, -3, 0").isEqualTo(-3);
        assertThat(heap.removeMin()).as("removeMin 3 of 4 after adding -3, 4, -3, 0").isEqualTo(0);
        assertThat(heap.removeMin()).as("removeMin 4 of 4 after adding -3, 4, -3, 0").isEqualTo(4);
    }

    @Test
    void peekDoesNotRemoveTheMinimum() {
        Solution heap = new Solution();
        heap.add(9);
        heap.add(2);
        assertThat(heap.peek()).as("first peek after adding 9, 2").isEqualTo(2);
        assertThat(heap.size()).as("size after adding 9, 2 and peeking").isEqualTo(2);
        assertThat(heap.peek()).as("second peek after adding 9, 2 must return the same value").isEqualTo(2);
        assertThat(heap.removeMin()).as("removeMin after adding 9, 2").isEqualTo(2);
        assertThat(heap.size()).as("size after adding 9, 2 and removing the minimum").isEqualTo(1);
    }

    @Test
    void emptyRemoveThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().removeMin())
                .as("removeMin() on an empty heap").isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void emptyPeekThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().peek())
                .as("peek() on an empty heap").isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void sizeAndEmptyTrackEveryRemoval() {
        Solution heap = new Solution();
        for (int i = 8; i >= 1; i--) {
            heap.add(i);
            assertThat(heap.isEmpty()).as("the heap must not be empty after adding %d", i).isFalse();
        }
        for (int i = 1; i <= 8; i++) {
            assertThat(heap.removeMin()).as("removeMin %d of 8 after adding 8..1", i).isEqualTo(i);
            assertThat(heap.size()).as("size after %d of 8 removals", i).isEqualTo(8 - i);
        }
        assertThat(heap.isEmpty()).as("the heap must be empty after removing all 8 values").isTrue();
    }
}
