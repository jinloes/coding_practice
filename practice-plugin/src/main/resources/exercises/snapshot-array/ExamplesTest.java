package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsValueFromEarlierSnapshotAfterLaterUpdate() {
        Solution arr = new Solution(3);
        arr.set(0, 5);
        assertThat(arr.snap()).as("first snap id").isEqualTo(0);
        arr.set(0, 6);
        assertThat(arr.get(0, 0)).as("get(0, 0) after later set").isEqualTo(5);
    }

    @Test
    void overwritesSameIndexMultipleTimesBeforeSnap() {
        Solution arr = new Solution(3);
        arr.set(1, 2);
        arr.set(1, 9);
        assertThat(arr.snap()).as("first snap id").isEqualTo(0);
        assertThat(arr.get(1, 0)).as("latest value before snap 0").isEqualTo(9);
    }

    @Test
    void returnsZeroForUnmodifiedIndex() {
        Solution arr = new Solution(3);
        assertThat(arr.snap()).as("first snap id").isEqualTo(0);
        assertThat(arr.get(2, 0)).as("untouched index defaults to 0").isEqualTo(0);
    }
}
