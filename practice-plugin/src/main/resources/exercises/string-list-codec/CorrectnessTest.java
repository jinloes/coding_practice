package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
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

    @Test
    void roundTripsEmptyList() {
        assertRoundTrip(List.of());
    }

    @Test
    void roundTripsSeveralEmptyEntries() {
        assertRoundTrip(List.of("", "", ""));
    }

    @Test
    void roundTripsSingleEntry() {
        assertRoundTrip(List.of("solo"));
    }

    @Test
    void roundTripsDigitsAndMarkers() {
        assertRoundTrip(List.of("3#abc", "12", "#", "0#", "5"));
    }

    @Test
    void roundTripsQuotesBackslashesAndNewlines() {
        assertRoundTrip(List.of("say \"hi\"", "back\\slash", "line1\nline2", "tab\t", " "));
    }

    @Test
    void roundTripsUnicode() {
        assertRoundTrip(List.of("caf\u00e9", "\u65e5\u672c\u8a9e", "\ud83d\ude00 emoji", "\u0000"));
    }

    @Test
    void roundTripsDuplicatesAndOrder() {
        assertRoundTrip(List.of("b", "a", "b", "a", "a"));
    }

    @Test
    void roundTripsEntriesThatLookLikeEncodedLists() {
        String inner = Solution.encode(List.of("x", "y"));
        assertRoundTrip(List.of(inner, Solution.encode(List.of())));
    }

    @Test
    void roundTripsLongEntries() {
        assertRoundTrip(List.of("z".repeat(10_000), "", "y".repeat(257)));
    }

    @Test
    void producesIndependentResults() {
        List<String> original = List.of("one", "two");
        String first = Solution.encode(original);
        Solution.encode(List.of("noise", ""));
        assertThat(Solution.decode(first)).isEqualTo(original);
        assertThat(Solution.decode(Solution.encode(original))).isEqualTo(original);
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
