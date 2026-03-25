package com.jinloes.regex;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ValidJavaTestPatterns implements ArgumentsProvider {

    private static final Stream<Arguments> PATTERNS = Stream.of(
        // --- Anchors ---
        arguments("^hello$", "hello", true),
        arguments("^hello$", "hello world", false),
        arguments("^\\d+$", "12345", true),
        arguments("^\\d+$", "123a5", false),

        // --- Literals & alternation ---
        arguments("cat|dog", "cat", true),
        arguments("cat|dog", "dog", true),
        arguments("cat|dog", "fish", false),

        // --- Quantifiers ---
        arguments("ab?c", "ac", true),
        arguments("ab?c", "abc", true),
        arguments("ab?c", "abbc", false),
        arguments("ab+c", "abc", true),
        arguments("ab+c", "abbbc", true),
        arguments("ab+c", "ac", false),
        arguments("ab*c", "ac", true),
        arguments("ab*c", "abbbc", true),
        arguments("abc{2}", "abcc", true),
        arguments("abc{2}", "abc", false),
        arguments("abc{2,4}", "abccc", true),
        arguments("abc{2,4}", "ab", false),

        // --- Character classes ---
        arguments("[aeiou]", "a", true),
        arguments("[aeiou]", "b", false),
        arguments("[a-z]+", "hello", true),
        arguments("[a-z]+", "Hello", false),
        arguments("[^a-z]+", "123", true),
        arguments("[^a-z]+", "abc", false),
        arguments("\\d+", "42", true),
        arguments("\\d+", "abc", false),
        arguments("\\w+", "hello_123", true),
        arguments("\\s+", "   ", true),
        arguments("\\s+", "abc", false),

        // --- Groups & non-capturing groups ---
        arguments("(abc)+", "abcabc", true),
        arguments("(abc)+", "ab", false),
        arguments("rub(?:y|le)", "ruby", true),
        arguments("rub(?:y|le)", "ruble", true),
        arguments("rub(?:y|le)", "rub", false),

        // --- Named groups ---
        arguments("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})", "2024-03-15", true),
        arguments("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})", "24-3-15", false),
        arguments("(?<test>abc)", "abc", true),

        // --- Backreferences ---
        arguments("(\\w+) \\1", "hello hello", true),
        arguments("(\\w+) \\1", "hello world", false),
        arguments("(sens|respons)e and \\1ibility", "response and responsibility", true),
        arguments("(sens|respons)e and \\1ibility", "sense and responsibility", false),

        // --- Lookahead & lookbehind ---
        arguments("\\d+(?= dollars)", "100 dollars", false),
        arguments("\\d+(?= dollars)", "100 euros", false),
        arguments("(?<=\\$)\\d+", "100", false),
        arguments("(?<=\\$)\\d+\\.\\d{2}", "$19.99", false), // full match requires $

        // --- Email (simplified) ---
        arguments("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}", "user@example.com", true),
        arguments("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}", "not-an-email", false),

        // --- IP address ---
        arguments("(\\d{1,3}\\.){3}\\d{1,3}", "192.168.1.1", true),
        arguments("(\\d{1,3}\\.){3}\\d{1,3}", "999.x.1.1", false),

        // --- Date (YYYY-MM-DD) ---
        arguments("\\d{4}-\\d{2}-\\d{2}", "2024-01-01", true),
        arguments("\\d{4}-\\d{2}-\\d{2}", "01-01-2024", false),

        // --- Hex color ---
        arguments("#[0-9a-fA-F]{6}", "#1aB2c3", true),
        arguments("#[0-9a-fA-F]{6}", "#GGGGGG", false),

        // --- Unicode ---
        arguments("\\p{InCJKUnifiedIdeographs}", "漢", true),
        arguments("\\p{InCJKUnifiedIdeographs}", "A", false),
        arguments("\\N{LATIN CAPITAL LETTER A}", "A", true),
        arguments("\\N{LATIN CAPITAL LETTER A}", "B", false),

        // --- Greedy vs possessive ---
        arguments("abc{1}", "abc", true),
        arguments("abc{2}", "abc", false),
        arguments("{1,3}[A-F]", "A", true),

        // --- Character union ---
        arguments("[0[^\\W\\d]]", "0", true),
        arguments("[0[^\\W\\d]]", "d", true),
        arguments("[0[^\\W\\d]]", "1", false)
    );

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return PATTERNS;
    }
}