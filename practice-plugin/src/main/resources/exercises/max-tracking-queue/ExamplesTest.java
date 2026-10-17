package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void tracksMaximumAcrossDequeues() { Solution queue = new Solution(); queue.enqueue(3); queue.enqueue(1); queue.enqueue(5); assertThat(queue.max()).isEqualTo(5); assertThat(queue.dequeue()).isEqualTo(3); assertThat(queue.max()).isEqualTo(5); }
    @Test
    void keepsDuplicateMaximums() { Solution queue = new Solution(); queue.enqueue(4); queue.enqueue(4); assertThat(queue.dequeue()).isEqualTo(4); assertThat(queue.max()).isEqualTo(4); }
    @Test
    void rejectsMaxOnEmptyQueue() { assertThatThrownBy(() -> new Solution().max()).isInstanceOf(NoSuchElementException.class); }
}
