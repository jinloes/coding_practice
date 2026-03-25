package com.jinloes.regex;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JavaRegexTest {

    @DisplayName("Java Regex Tests")
    @ParameterizedTest
    @ArgumentsSource(ValidJavaTestPatterns.class)
    void test(String pattern, String input, boolean expected) {
        assertThat(Pattern.compile(pattern, Pattern.MULTILINE).matcher(input).matches())
            .isEqualTo(expected);
    }

    @DisplayName("Java regex invalid patterns")
    @ParameterizedTest
    @ArgumentsSource(InvalidJavaPcre2Patterns.class)
    void testInvalidPatterns(String pattern, String input) {
        assertThatThrownBy(() ->
            Pattern.compile(pattern, Pattern.MULTILINE).matcher(input).matches())
            .isInstanceOf(PatternSyntaxException.class);
    }
}