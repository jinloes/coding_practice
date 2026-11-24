package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void emptyInputHasNoElementsAndThrowsOnNext() {
        Solution iterator = new Solution(List.of());
        assertThat(iterator.hasNext()).isFalse();
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void onlyEmptyNestedListsReturnsFalseFromHasNext() {
        Solution iterator = new Solution(List.of(
                Solution.NestedValue.ofList(List.of(Solution.NestedValue.ofList(List.of()))),
                Solution.NestedValue.ofList(List.of())));
        assertThat(iterator.hasNext()).isFalse();
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void repeatedHasNextCallsDoNotAdvanceIterator() {
        Solution iterator = new Solution(List.of(
                Solution.NestedValue.ofList(List.of(Solution.NestedValue.ofInt(10))),
                Solution.NestedValue.ofInt(20)));
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(10);
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isEqualTo(20);
        assertThat(iterator.hasNext()).isFalse();
    }

    @Test
    void callingNextDirectlyWithoutHasNextWorks() {
        Solution iterator = new Solution(List.of(
                Solution.NestedValue.ofList(List.of()),
                Solution.NestedValue.ofList(List.of(Solution.NestedValue.ofInt(7)))));
        assertThat(iterator.next()).isEqualTo(7);
        assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
    }
}
