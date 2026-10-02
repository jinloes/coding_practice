package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExamplesTest {
    @Test
    void tracksTheMedianAsValuesArrive() {
        Solution stream = new Solution();
        stream.add(5);
        assertThat(stream.median()).as("median of [5]").isEqualTo(5.0);
        stream.add(15);
        assertThat(stream.median()).as("median of [5, 15]").isEqualTo(10.0);
        stream.add(1);
        assertThat(stream.median()).as("median of [5, 15, 1]").isEqualTo(5.0);
    }

    @Test
    void rejectsAMedianRequestBeforeAnyValueArrives() {
        Solution stream = new Solution();
        assertThatThrownBy(stream::median).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void handlesNegativeAndRepeatedValues() {
        Solution stream = new Solution();
        stream.add(-1);
        stream.add(-1);
        assertThat(stream.median()).as("median of [-1, -1]").isEqualTo(-1.0);
        stream.add(4);
        assertThat(stream.median()).as("median of [-1, -1, 4]").isEqualTo(-1.0);
        stream.add(4);
        assertThat(stream.median()).as("median of [-1, -1, 4, 4]").isEqualTo(1.5);
    }
}
