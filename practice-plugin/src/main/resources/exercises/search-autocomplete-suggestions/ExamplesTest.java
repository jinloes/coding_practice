package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void returnsUpToThreeLexicographicMatchesPerPrefix() {
        String[] products = {"mobile", "mouse", "moneypot", "monitor", "mousepad"};
        assertThat(Solution.suggest(products, "mouse"))
                .as("mouse query suggestions")
                .containsExactly(
                        List.of("mobile", "moneypot", "monitor"),
                        List.of("mobile", "moneypot", "monitor"),
                        List.of("mouse", "mousepad"),
                        List.of("mouse", "mousepad"),
                        List.of("mouse", "mousepad"));
    }

    @Test
    void returnsSingleMatchingProductForEveryPrefix() {
        assertThat(Solution.suggest(new String[] {"haven"}, "haven"))
                .as("single product exact match")
                .containsExactly(
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"),
                        List.of("haven"));
    }

    @Test
    void returnsEmptyListsWhenNoProductsMatch() {
        assertThat(Solution.suggest(new String[] {"bag", "ball"}, "cat"))
                .as("no matching prefixes")
                .containsExactly(List.of(), List.of(), List.of());
    }
}
