package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void carriesValuesAcrossMultipleEmptySnapshots() {
        Solution arr = new Solution(4);
        arr.set(2, 42);
        assertThat(arr.snap()).isEqualTo(0);
        assertThat(arr.snap()).isEqualTo(1);
        assertThat(arr.snap()).isEqualTo(2);
        arr.set(2, -7);
        assertThat(arr.snap()).isEqualTo(3);

        assertThat(arr.get(2, 0)).as("snap 0").isEqualTo(42);
        assertThat(arr.get(2, 1)).as("snap 1 carries forward").isEqualTo(42);
        assertThat(arr.get(2, 2)).as("snap 2 carries forward").isEqualTo(42);
        assertThat(arr.get(2, 3)).as("snap 3 sees new negative value").isEqualTo(-7);
        assertThat(arr.get(0, 3)).as("other index still 0").isEqualTo(0);
    }

    @Test
    void supportsSettingBackToZeroAndQueryingInterleavedVersions() {
        Solution arr = new Solution(2);
        arr.set(0, 10);
        arr.snap(); // 0 -> [10, 0]
        arr.set(0, 0);
        arr.set(1, 20);
        arr.snap(); // 1 -> [0, 20]
        arr.set(1, 30);
        arr.set(1, 40);
        arr.snap(); // 2 -> [0, 40]

        assertThat(arr.get(0, 0)).isEqualTo(10);
        assertThat(arr.get(0, 1)).isEqualTo(0);
        assertThat(arr.get(0, 2)).isEqualTo(0);
        assertThat(arr.get(1, 0)).isEqualTo(0);
        assertThat(arr.get(1, 1)).isEqualTo(20);
        assertThat(arr.get(1, 2)).isEqualTo(40);
    }

    @Test
    void handlesManySnapshotsOnSingleIndexWithoutLosingEarlyHistory() {
        Solution arr = new Solution(1);
        for (int s = 0; s < 200; s++) {
            if (s % 3 == 0) {
                arr.set(0, s * 10);
            }
            assertThat(arr.snap()).isEqualTo(s);
        }
        for (int s = 0; s < 200; s++) {
            int expected = (s / 3) * 3 * 10;
            assertThat(arr.get(0, s)).as("snap %d", s).isEqualTo(expected);
        }
    }

    @Test
    void handlesLargeSparseArrayWithFewWrites() {
        Solution arr = new Solution(100_000);
        arr.set(99_999, 77);
        int s0 = arr.snap();
        arr.set(99_999, 88);
        int s1 = arr.snap();
        assertThat(arr.get(99_999, s0)).isEqualTo(77);
        assertThat(arr.get(99_999, s1)).isEqualTo(88);
        assertThat(arr.get(50_000, s1)).isEqualTo(0);
    }
}
