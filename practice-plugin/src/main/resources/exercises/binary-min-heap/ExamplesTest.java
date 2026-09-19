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
        assertThat(heap.peek()).isEqualTo(2);
        assertThat(heap.removeMin()).isEqualTo(2);
        assertThat(heap.removeMin()).isEqualTo(5);
        assertThat(heap.removeMin()).isEqualTo(7);
    }

    @Test
    void returnsDuplicateMinimaIndividually() {
        Solution heap = new Solution();
        heap.add(4);
        heap.add(1);
        heap.add(1);
        assertThat(heap.removeMin()).isEqualTo(1);
        assertThat(heap.removeMin()).isEqualTo(1);
    }

    @Test
    void emptyOperationsThrow() {
        Solution heap = new Solution();
        assertThat(heap.isEmpty()).isTrue();
        assertThatThrownBy(heap::removeMin).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(heap::peek).isInstanceOf(NoSuchElementException.class);
    }
}
