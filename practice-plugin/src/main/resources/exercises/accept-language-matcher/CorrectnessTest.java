package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void handlesEmptyHeaderOrSupportedList() {
        assertThat(Solution.matchLanguages("", List.of("en-US"))).as("empty header").isEmpty();
        assertThat(Solution.matchLanguages(" , , ", List.of("en-US"))).as("blank entries").isEmpty();
        assertThat(Solution.matchLanguages("en-US, *", List.of())).as("empty supported").isEmpty();
    }

    @Test
    void matchesCaseInsensitivelyWhilePreservingSupportedCasing() {
        assertThat(Solution.matchLanguages("EN-us, FR", List.of("fr-CA", "en-US", "fr-FR")))
                .as("case-insensitive exact and base matching")
                .containsExactly("en-US", "fr-CA", "fr-FR");
    }

    @Test
    void avoidsDuplicatesAcrossSpecificBaseAndWildcard() {
        assertThat(Solution.matchLanguages("en-US, en, *, en-GB", List.of("en-GB", "en-US", "es-ES")))
                .as("en-US selected first, then en-GB via base 'en', then es-ES via '*'")
                .containsExactly("en-US", "en-GB", "es-ES");
    }

    @Test
    void doesNotConfuseSharedPrefixesWithBaseLanguage() {
        assertThat(Solution.matchLanguages("en", List.of("eno-US", "en-US", "en")))
                .as("'en' must not match 'eno-US'")
                .containsExactly("en-US", "en");
    }

    @Test
    void regionTagDoesNotMatchDifferentRegionOrBaseOnly() {
        assertThat(Solution.matchLanguages("fr-CA", List.of("fr", "fr-FR", "en-US")))
                .as("specific region tag must not fall back to base or other region")
                .isEmpty();
    }
}
