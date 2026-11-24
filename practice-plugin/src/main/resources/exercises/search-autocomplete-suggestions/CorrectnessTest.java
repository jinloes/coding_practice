package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void doesNotMutateCallerProductsArray() {
        String[] products = {"zebra", "apple", "apricot", "avocado"};
        String[] original = products.clone();
        List<List<String>> result = Solution.suggest(products, "ap");
        assertThat(result).containsExactly(
                List.of("apple", "apricot", "avocado"),
                List.of("apple", "apricot"));
        assertThat(products).as("caller array must remain unchanged").containsExactly(original);
    }

    @Test
    void handlesQueryLongerThanMatchingProducts() {
        String[] products = {"ab", "abc", "abcd", "abce"};
        assertThat(Solution.suggest(products, "abcde"))
                .as("shorter words drop out once prefix exceeds their length")
                .containsExactly(
                        List.of("ab", "abc", "abcd"),
                        List.of("ab", "abc", "abcd"),
                        List.of("abc", "abcd", "abce"),
                        List.of("abcd"),
                        List.of());
    }

    @Test
    void handlesEmptyProductCatalog() {
        assertThat(Solution.suggest(new String[0], "abc"))
                .as("empty catalog yields empty list per character")
                .containsExactly(List.of(), List.of(), List.of());
    }

    @Test
    void narrowsWindowToEmptyAndStaysEmptyForRemainingPrefixes() {
        String[] products = {"bags", "baggage", "banner", "box", "cloths"};
        assertThat(Solution.suggest(products, "bagsz"))
                .as("prefix diverges at final character")
                .containsExactly(
                        List.of("baggage", "bags", "banner"),
                        List.of("baggage", "bags", "banner"),
                        List.of("baggage", "bags"),
                        List.of("bags"),
                        List.of());
    }
}
