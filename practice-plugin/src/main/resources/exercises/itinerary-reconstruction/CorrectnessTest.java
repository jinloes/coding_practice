package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noTicketsStaysHome() {
        assertThat(Solution.buildItinerary(tickets(), "HOME"))
                .as("buildItinerary from HOME")
                .containsExactly("HOME");
    }

    @Test
    void walksAWholeCycle() {
        assertThat(Solution.buildItinerary(tickets("AAA-BBB", "BBB-CCC", "CCC-AAA"), "AAA"))
                .as("buildItinerary from AAA")
                .containsExactly("AAA", "BBB", "CCC", "AAA");
    }

    @Test
    void repeatedTicketsAreUsedTwice() {
        assertThat(Solution.buildItinerary(tickets("AAA-BBB", "AAA-BBB", "BBB-AAA"), "AAA"))
                .as("buildItinerary from AAA")
                .containsExactly("AAA", "BBB", "AAA", "BBB");
    }

    @Test
    void deadEndBranchComesLast() {
        assertThat(Solution.buildItinerary(tickets("DEN-ELP", "DEN-BOS", "BOS-DEN"), "DEN"))
                .as("buildItinerary from DEN")
                .containsExactly("DEN", "BOS", "DEN", "ELP");
    }

    @Test
    void selfLoopsAreTickets() {
        assertThat(Solution.buildItinerary(tickets("AAA-AAA", "AAA-BBB"), "AAA"))
                .as("buildItinerary from AAA")
                .containsExactly("AAA", "AAA", "BBB");
    }

    @Test
    void chainsOfLoopsPickSmallestFirst() {
        assertThat(Solution.buildItinerary(tickets("AAA-DDD", "AAA-CCC", "CCC-AAA", "DDD-EEE", "EEE-AAA"), "AAA"))
                .as("buildItinerary from AAA")
                .containsExactly("AAA", "CCC", "AAA", "DDD", "EEE", "AAA");
    }

    @Test
    void pathDoesNotEndWhereItStarted() {
        assertThat(Solution.buildItinerary(tickets("MMM-NNN", "NNN-OOO", "NNN-MMM", "MMM-PPP", "OOO-NNN"), "MMM"))
                .as("buildItinerary from MMM")
                .containsExactly("MMM", "NNN", "OOO", "NNN", "MMM", "PPP");
    }

    @Test
    void longHubItineraryAvoidsDeepRecursion() {
        int trips = 2_000;
        List<List<String>> all = new ArrayList<>();
        List<String> expected = new ArrayList<>();
        expected.add("HUB");
        for (int i = 0; i < trips; i++) {
            String city = String.format("C%05d", (i * 7) % trips);
            all.add(List.of("HUB", city));
            all.add(List.of(city, "HUB"));
        }
        for (int i = 0; i < trips; i++) {
            expected.add(String.format("C%05d", i));
            expected.add("HUB");
        }
        assertThat(Solution.buildItinerary(all, "HUB")).as("round trips to %s cities", trips).isEqualTo(expected);
    }

    @Test
    void doesNotChangeTheTickets() {
        List<List<String>> all = tickets("AAA-BBB", "BBB-AAA");
        Solution.buildItinerary(all, "AAA");
        assertThat(all).containsExactly(List.of("AAA", "BBB"), List.of("BBB", "AAA"));
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
