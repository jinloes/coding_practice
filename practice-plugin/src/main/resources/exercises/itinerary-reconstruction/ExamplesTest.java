package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void doesNotGetStrandedAtTheSmallestDestination() {
        assertThat(Solution.buildItinerary(tickets("SEA-PDX", "PDX-SEA", "SEA-LAX"), "SEA"))
                .as("buildItinerary from SEA")
                .containsExactly("SEA", "PDX", "SEA", "LAX");
    }

    @Test
    void singleTicket() {
        assertThat(Solution.buildItinerary(tickets("AAA-BBB"), "AAA"))
                .as("buildItinerary from AAA")
                .containsExactly("AAA", "BBB");
    }

    @Test
    void hubWithTwoRoundTrips() {
        assertThat(Solution.buildItinerary(tickets("HUB-CCC", "HUB-AAA", "AAA-HUB", "CCC-HUB"), "HUB"))
                .as("buildItinerary from HUB")
                .containsExactly("HUB", "AAA", "HUB", "CCC", "HUB");
    }

    private static List<List<String>> tickets(String... pairs) {
        List<List<String>> result = new ArrayList<>();
        for (String pair : pairs) {
            String[] ends = pair.split("-");
            result.add(List.of(ends[0], ends[1]));
        }
        return result;
    }
}
