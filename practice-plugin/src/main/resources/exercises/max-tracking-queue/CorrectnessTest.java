package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void dequeuesInFifoOrder() { Solution q = new Solution(); q.enqueue(1); q.enqueue(2); q.enqueue(3); assertThat(q.dequeue()).isEqualTo(1); assertThat(q.dequeue()).isEqualTo(2); assertThat(q.dequeue()).isEqualTo(3); }
    @Test
    void updatesMaxWhenMaximumLeaves() { Solution q = new Solution(); q.enqueue(9); q.enqueue(2); q.enqueue(7); assertThat(q.max()).isEqualTo(9); q.dequeue(); assertThat(q.max()).isEqualTo(7); }
    @Test
    void handlesNegativeValues() { Solution q = new Solution(); q.enqueue(-5); q.enqueue(-2); q.enqueue(-7); assertThat(q.max()).isEqualTo(-2); }
    @Test
    void rejectsDequeueOnEmptyQueue() { assertThatThrownBy(() -> new Solution().dequeue()).isInstanceOf(NoSuchElementException.class); }
    @Test
    void matchesReferenceForSeededOperations() { Solution q = new Solution(); Deque<Integer> expected = new ArrayDeque<>(); for (int i = 0; i < 300; i++) { int value = (i * 37) % 101 - 50; q.enqueue(value); expected.addLast(value); if (i % 4 == 0) assertThat(q.dequeue()).isEqualTo(expected.removeFirst()); if (!expected.isEmpty()) assertThat(q.max()).isEqualTo(expected.stream().mapToInt(Integer::intValue).max().orElseThrow()); } }
}
