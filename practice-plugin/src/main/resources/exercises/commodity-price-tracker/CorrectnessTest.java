package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void correctingLatestTimestampUpdatesLatestMinAndMax() {
        Solution tracker = new Solution();
        tracker.update(10, 100);
        assertThat(tracker.latest()).isEqualTo(100);
        assertThat(tracker.minPrice()).isEqualTo(100);
        assertThat(tracker.maxPrice()).isEqualTo(100);
        tracker.update(10, 40);
        assertThat(tracker.latest()).isEqualTo(40);
        assertThat(tracker.minPrice()).isEqualTo(40);
        assertThat(tracker.maxPrice()).isEqualTo(40);
    }

    @Test
    void repeatedCorrectionsRemoveStaleExtremes() {
        Solution tracker = new Solution();
        tracker.update(1, 50);
        tracker.update(2, 100);
        tracker.update(3, 10);
        tracker.update(2, 60);
        tracker.update(3, 45);
        assertThat(tracker.maxPrice()).isEqualTo(60);
        assertThat(tracker.minPrice()).isEqualTo(45);
        assertThat(tracker.latest()).isEqualTo(45);
    }

    @Test
    void overwritingWithSamePriceIsIdempotent() {
        Solution tracker = new Solution();
        tracker.update(1, 25);
        tracker.update(1, 25);
        tracker.update(2, 30);
        tracker.update(1, 35);
        assertThat(tracker.minPrice()).isEqualTo(30);
        assertThat(tracker.maxPrice()).isEqualTo(35);
    }
}
