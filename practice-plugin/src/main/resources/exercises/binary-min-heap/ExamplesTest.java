package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void removesValuesInAscendingOrder() {
        Solution heap = new Solution();
        heap.add(7);
        heap.add(2);
        heap.add(5);
        assertThat(heap.peek()).as("peek after adding 7, 2, 5").isEqualTo(2);
        assertThat(heap.removeMin()).as("removeMin 1 of 3 after adding 7, 2, 5").isEqualTo(2);
        assertThat(heap.removeMin()).as("removeMin 2 of 3 after adding 7, 2, 5").isEqualTo(5);
        assertThat(heap.removeMin()).as("removeMin 3 of 3 after adding 7, 2, 5").isEqualTo(7);
    }

    @Test
    void returnsDuplicateMinimaIndividually() {
        Solution heap = new Solution();
        heap.add(4);
        heap.add(1);
        heap.add(1);
        assertThat(heap.removeMin()).as("first removeMin after adding 4, 1, 1").isEqualTo(1);
        assertThat(heap.removeMin()).as("second removeMin after adding 4, 1, 1").isEqualTo(1);
    }

    @Test
    void emptyOperationsThrow() {
        Solution heap = new Solution();
        assertThat(heap.isEmpty()).as("a new heap must be empty").isTrue();
        assertThatThrownBy(heap::removeMin).as("removeMin() on an empty heap")
                .isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(heap::peek).as("peek() on an empty heap")
                .isInstanceOf(NoSuchElementException.class);
    }
}
