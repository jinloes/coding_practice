package com.jinloes.regex;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PCRE2EngineTest {

    private final PCRE2Engine engine = new PCRE2Engine();

    @Test
    void findsAllWords() {
        assertThat(engine.findMatches("foo bar baz", "\\w+"))
            .isEqualTo(List.of("foo", "bar", "baz"));
    }

    @Test
    void findsAllIntegers() {
        assertThat(engine.findMatches("order 3 items at 10 dollars each", "\\d+"))
            .isEqualTo(List.of("3", "10"));
    }

    @Test
    void findsDecimalPrices() {
        assertThat(engine.findMatches("pay $19.99 and $5.00", "\\d+\\.\\d{2}"))
            .isEqualTo(List.of("19.99", "5.00"));
    }

    @Test
    void findsDates() {
        assertThat(engine.findMatches("from 2024-01-15 to 2024-03-31", "\\d{4}-\\d{2}-\\d{2}"))
            .isEqualTo(List.of("2024-01-15", "2024-03-31"));
    }

    @Test
    void findsEmailAddresses() {
        assertThat(engine.findMatches("contact alice@example.com or bob@test.org for info",
            "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}"))
            .isEqualTo(List.of("alice@example.com", "bob@test.org"));
    }

    @Test
    void findsCaseInsensitiveWithInlineFlag() {
        assertThat(engine.findMatches("Hello WORLD hello world", "(?i)hello"))
            .isEqualTo(List.of("Hello", "hello"));
    }

    @Test
    void findsUnicodeCharacters() {
        assertThat(engine.findMatches("CJK: 漢 字", "\\p{han}"))
            .isEqualTo(List.of("漢", "字"));
    }

    @Test
    void returnsEmptyListWhenNoMatches() {
        assertThat(engine.findMatches("hello world", "\\d+"))
            .isEmpty();
    }

    @Test
    void returnsEmptyListForEmptyInput() {
        assertThat(engine.findMatches("", "\\w+"))
            .isEmpty();
    }

    @Test
    void findsMatchesWithJit() {
        assertThat(engine.findMatches("foo bar baz", "\\w+", true))
            .isEqualTo(List.of("foo", "bar", "baz"));
    }

    @Test
    void findsUnicodeCharactersWithJit() {
        assertThat(engine.findMatches("CJK: 漢 字", "\\p{han}", true))
            .isEqualTo(List.of("漢", "字"));
    }
}