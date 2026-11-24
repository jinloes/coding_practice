package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void ordersExactMatchesByClientHeader() {
        assertThat(Solution.matchLanguages("en-US, fr-CA", List.of("fr-CA", "en-GB", "en-US")))
                .as("exact matches follow client preference order")
                .containsExactly("en-US", "fr-CA");
    }

    @Test
    void expandsBaseLanguageInSupportedOrder() {
        assertThat(Solution.matchLanguages("en, fr-CA", List.of("fr-FR", "en-GB", "en-US", "fr-CA")))
                .as("base language 'en' matches en-GB then en-US before fr-CA")
                .containsExactly("en-GB", "en-US", "fr-CA");
    }

    @Test
    void wildcardAppendsRemainingSupportedTags() {
        assertThat(Solution.matchLanguages("de-DE, *", List.of("en-US", "fr-FR")))
                .as("wildcard appends all unmatched supported tags")
                .containsExactly("en-US", "fr-FR");
    }
}
