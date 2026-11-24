package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void updatesCorrectEarlierTimestampAndRefreshMinMax() {
        Solution tracker = new Solution();
        tracker.update(1, 10);
        tracker.update(2, 5);
        assertThat(tracker.latest()).isEqualTo(5);
        assertThat(tracker.maxPrice()).isEqualTo(10);
        tracker.update(1, 3);
        assertThat(tracker.maxPrice()).isEqualTo(5);
        tracker.update(4, 2);
        assertThat(tracker.minPrice()).isEqualTo(2);
    }

    @Test
    void keepsDuplicatePriceWhenOnlyOneTimestampChanges() {
        Solution tracker = new Solution();
        tracker.update(1, 10);
        tracker.update(2, 10);
        tracker.update(1, 4);
        assertThat(tracker.maxPrice()).isEqualTo(10);
        assertThat(tracker.minPrice()).isEqualTo(4);
        assertThat(tracker.latest()).isEqualTo(10);
    }

    @Test
    void outOfOrderTimestampsDoNotChangeLatest() {
        Solution tracker = new Solution();
        tracker.update(5, 20);
        tracker.update(2, 50);
        assertThat(tracker.latest()).isEqualTo(20);
        assertThat(tracker.maxPrice()).isEqualTo(50);
        assertThat(tracker.minPrice()).isEqualTo(20);
    }
}
