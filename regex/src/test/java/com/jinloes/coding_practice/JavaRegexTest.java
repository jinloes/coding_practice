package com.jinloes.coding_practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;

public class JavaRegexTest {

  @DisplayName("Java Regex Tests")
  @ParameterizedTest
  @FieldSource("validPatterns")
  public void test(String pattern, Integer flags, String toTest, boolean expected) {
    flags = Objects.requireNonNullElse(flags, 0);
    var matcher = Pattern.compile(pattern, flags)
        .matcher(toTest);
    assertThat(matcher.matches())
        .isEqualTo(expected);
  }

  @DisplayName("Java regex invalid patterns")
  @ParameterizedTest
  @FieldSource("invalidPatterns")
  public void testInvalidPatterns(String pattern, Integer flags, String toTest) {
    assertThatThrownBy(() ->
        Pattern.compile(pattern, Objects.requireNonNullElse(flags, 0))
            .matcher(toTest)
            .matches())
        .isInstanceOf(PatternSyntaxException.class);
  }

  private static final List<Arguments> validPatterns = List.of(
      // Named groups
      arguments("(?<test>abc)", null, "abc", true),
      //Greedy
      arguments("abc{1}", null, "abc", true),
      arguments("abc{2}", null, "abc", false),
      arguments("abc{1}", null, "abcabc", false),
      arguments("rub(?:y|le)", null, "ruby", true),
      arguments("rub(?:y|le)", null, "ruble", true),
      arguments("rub(?:y|le)", null, "rub", false),
      // Backreference
      arguments("(sens|respons)e and \\1ibility", null, "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibilitys", null, "response and responsibility", false),
      // Character Union
      arguments("[0[^\\W\\d]]", null, "0", true),
      arguments("[0[^\\W\\d]]", null, "d", true),
      arguments("[0[^\\W\\d]]", null, "1", false),
      arguments("\\N{LATIN CAPITAL LETTER A}", null, "A", true),
      arguments("\\N{LATIN CAPITAL LETTER A}", null, "B", false)
  );

  private static final List<Arguments> invalidPatterns = List.of(
      //PERL5
      // Code
      arguments("R(?{1=1})", null, "R"),
      arguments("R(??{1=1})", null, "R"),
      //PCRE
      // Comment
      arguments("R(?#comment)", null, "R"),
      // Backreference
      arguments("(sens|respons)e and \\g{1}ibility", null, "response and responsibility"),
      arguments("(sens|respons)e and \\g1ibility", null, "response and responsibility"),
      // Conditional
      arguments("(START)?\\w+(?(1)foo|bar)", null, "STARTabcfoo"),
      arguments("((?(R1)a|b))", null, "b"),
      arguments("(?(VERSION>=10.4)a|b)", null, "a"),
      // Char Unicode
      arguments("\\N{U+0041}", null, "A"),
      // Named groups
      arguments("(?(DEFINE)(?<test>a))(?&test)", null, "a")
  );
}
