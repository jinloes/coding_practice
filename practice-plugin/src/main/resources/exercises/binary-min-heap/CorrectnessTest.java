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
        assertThat(heap.isEmpty()).isTrue();
        assertThat(heap.size()).isZero();
    }

    @Test
    void handlesSingletonAndBoundaryValues() {
        Solution heap = new Solution();
        heap.add(Integer.MAX_VALUE);
        heap.add(Integer.MIN_VALUE);
        assertThat(heap.peek()).isEqualTo(Integer.MIN_VALUE);
        assertThat(heap.removeMin()).isEqualTo(Integer.MIN_VALUE);
        assertThat(heap.removeMin()).isEqualTo(Integer.MAX_VALUE);
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
                assertThat(heap.removeMin()).isEqualTo(oracle.remove());
            }
            assertThat(heap.size()).isEqualTo(oracle.size());
            assertThat(heap.peek()).isEqualTo(oracle.peek());
        }
        while (!oracle.isEmpty()) {
            assertThat(heap.removeMin()).isEqualTo(oracle.remove());
        }
        assertThat(heap.isEmpty()).isTrue();
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
        assertThat(heap.size()).isEqualTo(512);
        while (!oracle.isEmpty()) {
            assertThat(heap.removeMin()).isEqualTo(oracle.remove());
        }
    }

    @Test
    void handlesDuplicatesAndNegativeValues() {
        Solution heap = new Solution();
        heap.add(-3);
        heap.add(4);
        heap.add(-3);
        heap.add(0);
        assertThat(heap.removeMin()).isEqualTo(-3);
        assertThat(heap.removeMin()).isEqualTo(-3);
        assertThat(heap.removeMin()).isEqualTo(0);
        assertThat(heap.removeMin()).isEqualTo(4);
    }

    @Test
    void peekDoesNotRemoveTheMinimum() {
        Solution heap = new Solution();
        heap.add(9);
        heap.add(2);
        assertThat(heap.peek()).isEqualTo(2);
        assertThat(heap.size()).isEqualTo(2);
        assertThat(heap.peek()).isEqualTo(2);
        assertThat(heap.removeMin()).isEqualTo(2);
        assertThat(heap.size()).isEqualTo(1);
    }

    @Test
    void emptyRemoveThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().removeMin())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void emptyPeekThrowsTheRequiredException() {
        assertThatThrownBy(() -> new Solution().peek())
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void sizeAndEmptyTrackEveryRemoval() {
        Solution heap = new Solution();
        for (int i = 8; i >= 1; i--) {
            heap.add(i);
            assertThat(heap.isEmpty()).isFalse();
        }
        for (int i = 1; i <= 8; i++) {
            assertThat(heap.removeMin()).isEqualTo(i);
            assertThat(heap.size()).isEqualTo(8 - i);
        }
        assertThat(heap.isEmpty()).isTrue();
    }
}
