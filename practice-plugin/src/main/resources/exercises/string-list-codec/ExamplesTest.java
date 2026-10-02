package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void roundTripsPlainWords() {
        assertRoundTrip(List.of("hello", "world"));
    }

    @Test
    void distinguishesEmptyEntryFromEmptyList() {
        assertRoundTrip(List.of(""));
    }

    @Test
    void roundTripsEntriesContainingSeparators() {
        assertRoundTrip(List.of("a,b", "c"));
    }

    private static void assertRoundTrip(List<String> original) {
        List<String> copy = new ArrayList<>(original);
        String encoded = Solution.encode(copy);
        assertThat(encoded).as("encode(%s) must not be null", original).isNotNull();
        assertThat(copy).as("encode must not mutate its input").isEqualTo(original);
        assertThat(Solution.decode(encoded))
                .as("decode(encode(%s)) with encoded form \"%s\"", original, encoded)
                .isEqualTo(original);
    }
}
