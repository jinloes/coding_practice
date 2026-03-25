package com.jinloes.regex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.FieldSource;
import org.pcre4j.regex.Matcher;
import org.pcre4j.regex.Pattern;

import java.util.List;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class PCRE4JTest {

    private final PCRE2Engine pcre2Engine = new PCRE2Engine();

@DisplayName("Pcre4j valid patterns")
    @ParameterizedTest
    @ArgumentsSource(ValidPCRE2TestPatterns.class)
    void testValidPatterns(String pattern, String toTest, boolean expected) {
        assertThat(Pattern.compile(pattern, Pattern.MULTILINE).matcher(toTest).matches())
            .isEqualTo(expected);
    }

    @DisplayName("Pcre4j invalid patterns")
    @ParameterizedTest
    @FieldSource("invalidPatterns")
    void testInvalidPatterns(String pattern, Integer flags, String toTest) {
        assertThatThrownBy(() ->
            Pattern.compile(pattern, Objects.requireNonNullElse(flags, 0))
                .matcher(toTest)
                .matches())
            .isInstanceOf(PatternSyntaxException.class);
    }

    @DisplayName("Pcre4j low level patterns")
    @ParameterizedTest
    @ArgumentsSource(ValidPCRE2TestPatterns.class)
    void testLowLevel(String regex, String toTest, boolean expected) {
        assertThat(pcre2Engine.test(toTest, null, regex, false) > 0).isEqualTo(expected);
    }

    // Documents patterns that compile but produce incorrect results in PCRE4J
    @DisplayName("Pcre4j false positives")
    @ParameterizedTest
    @FieldSource("falsePositives")
    void testFalsePositives(String pattern, Integer flags, String toTest, boolean expected) {
        Matcher matcher = Pattern.compile(pattern, Objects.requireNonNullElse(flags, 0)).matcher(toTest);
        assertThat(matcher.matches()).isEqualTo(!expected);
    }

    private static final List<Arguments> invalidPatterns = List.of(
        // PERL5 code execution — not supported
        arguments("R(?{1 == 1})", null, "R"),
        // Unicode char name — not supported
        arguments("\\N{LATIN CAPITAL LETTER A}", null, "A")
    );

    // compiles but produces incorrect result
    private static final List<Arguments> falsePositives = List.of(
        // Character union — PCRE4J incorrectly returns false
        arguments("[0[^\\W\\d]]", null, "0", true)
    );
}