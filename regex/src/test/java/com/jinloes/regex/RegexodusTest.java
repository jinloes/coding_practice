package com.jinloes.regex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import regexodus.Pattern;
import regexodus.REFlags;

public class RegexodusTest {

  @DisplayName("Regexodous Tests")
  @ParameterizedTest
  @FieldSource("args")
  public void test(String regex, Integer flags, String testString, boolean expected) {
    flags = Objects.requireNonNullElse(flags, 0);
    Pattern pattern = new Pattern(regex, flags);
    assertThat(pattern.matches(testString))
        .isEqualTo(expected);
  }

  @DisplayName("Regexodous failing pattenrs")
  @ParameterizedTest
  @FieldSource("failingPatterns")
  public void testFailingPatterns(String regex, Integer flags, String testString) {
    assertThatThrownBy(() -> {
      var pattern = new Pattern(regex, Objects.requireNonNullElse(flags, 0));
      pattern.matches(testString);
    })
        .isNotNull();
  }

  private static final List<Arguments> args = List.of(
      arguments("foo.*?", null, "foo", true),
      arguments("foo.*?", null, "bar", false),
      arguments("foo.*?", REFlags.IGNORE_CASE, "Foo", true),
      arguments("foo baz", REFlags.IGNORE_SPACES, "foobaz", true),
      arguments("foo(?#comment)bar", null, "foobar", true),
      arguments("(sens|respons)e and \\1ibility", null, "response and responsibility", true),
      arguments("(sens|respons)e and \\g{1}ibility", null, "response and responsibility", false)//Not supported
  );

  private static final List<Arguments> failingPatterns = List.of(
      arguments("(?(1)foo|bar)", null, "foobar"),
      arguments("R(?{1=1})", null, "R"),
      arguments("\\N{LATIN CAPITAL LETTER A}", null, "A"),
      arguments("\\N{U+0041}", null, "A")
  );
}
