package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void tracksTheSecondLargestAsValuesArrive() {
        Solution stream = new Solution(2, new int[]{10, 3, 7});
        assertThat(stream.add(4)).as("k=2 over [10, 3, 7]: add(4)").isEqualTo(7);
        assertThat(stream.add(12)).as("k=2 over [10, 3, 7]: add(12)").isEqualTo(10);
        assertThat(stream.add(8)).as("k=2 over [10, 3, 7]: add(8)").isEqualTo(10);
        assertThat(stream.add(15)).as("k=2 over [10, 3, 7]: add(15)").isEqualTo(12);
    }

    @Test
    void kEqualsOneTracksTheMaximum() {
        Solution stream = new Solution(1, new int[]{});
        assertThat(stream.add(-5)).as("k=1 over []: add(-5)").isEqualTo(-5);
        assertThat(stream.add(-9)).as("k=1 over []: add(-9)").isEqualTo(-5);
        assertThat(stream.add(2)).as("k=1 over []: add(2)").isEqualTo(2);
    }

    @Test
    void repeatedValuesCountSeparately() {
        Solution stream = new Solution(2, new int[]{6, 6});
        assertThat(stream.add(6)).as("k=2 over [6, 6]: add(6)").isEqualTo(6);
        assertThat(stream.add(1)).as("k=2 over [6, 6]: add(1)").isEqualTo(6);
        assertThat(stream.add(9)).as("k=2 over [6, 6]: add(9)").isEqualTo(6);
    }

}
