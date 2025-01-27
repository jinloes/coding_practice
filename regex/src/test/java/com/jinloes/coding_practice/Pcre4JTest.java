package com.jinloes.coding_practice;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileContext;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.Pcre2Newline;
import org.pcre4j.Pcre4j;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;
import org.pcre4j.jna.Pcre2;
import org.pcre4j.regex.Matcher;
import org.pcre4j.regex.Pattern;

public class Pcre4JTest {
  private IPcre2 api;

  @BeforeAll
  public static void setup() {
    Pcre4j.setup(new Pcre2("libpcre2-8.dylib", "_8"));
  }

  @BeforeEach
  public void setUp() {
    api = Pcre4j.api();
  }

  @DisplayName("Pcre4j valid patterns")
  @ParameterizedTest
  @FieldSource("validPatterns")
  public void testValidPatterns(String pattern, Integer flags, String toTest, boolean expected) {
    flags = Objects.requireNonNullElse(flags, 0);
    Matcher matcher = Pattern.compile(pattern, flags)
        .matcher(toTest);
    assertThat(matcher.matches())
        .isEqualTo(expected);
  }

  @DisplayName("Pcre4j invalid patterns")
  @ParameterizedTest
  @FieldSource("invalidPatterns")
  public void testInvalidPatterns(String pattern, Integer flags, String toTest) {
    assertThatThrownBy(() ->
        Pattern.compile(pattern, Objects.requireNonNullElse(flags, 0))
            .matcher(toTest)
            .matches())
        .isInstanceOf(PatternSyntaxException.class);
  }

  @DisplayName("Pcre4j low level patterns")
  @ParameterizedTest
  @FieldSource("lowLevelPatterns")
  public void testLowLevel(String regex, EnumSet<Pcre2CompileOption> options, String toTest, boolean expected) {
    var compileContext = new Pcre2CompileContext(api, null);
    compileContext.setNewline(Pcre2Newline.ANY);
    var code = new Pcre2Code(api, regex, options, compileContext);
    var matchData = new Pcre2MatchData(code);
    code.match(toTest, 0, EnumSet.noneOf(Pcre2MatchOption.class), matchData, null);
    var groups = Pcre4jUtils.getMatchGroups(code, toTest, matchData);

    assertThat(ArrayUtils.isNotEmpty(groups))
        .isEqualTo(expected);
  }

  @DisplayName("Pcre4j false positives")
  @ParameterizedTest
  @FieldSource("falsePositives")
  public void testFalsePositives(String pattern, Integer flags, String toTest, boolean expected) {
    flags = Objects.requireNonNullElse(flags, 0);
    Matcher matcher = Pattern.compile(pattern, flags)
        .matcher(toTest);
    assertThat(matcher.matches())
        .isEqualTo(!expected);
  }

  private static final List<Arguments> lowLevelPatterns = List.of(
      // PERL5
      // Ignore whitespace
      arguments("[d-eg-i3-7]", EnumSet.of(Pcre2CompileOption.UTF), "dg37", true),
      arguments("[d-e g-i 3-7]", EnumSet.of(Pcre2CompileOption.UTF, Pcre2CompileOption.EXTENDED_MORE), "dg37", true)
  );

  private static final List<Arguments> validPatterns = List.of(
      arguments("foo", null, "foo", true),
      arguments("foo.*?", null, "foo", true),
      arguments("foo.*?", null, "bar", false),
      arguments("foo.*?", Pattern.CASE_INSENSITIVE, "Foo", true),
      // Conditional
      arguments("(START)?\\w+(?(1)foo|bar)", null, "STARTabcfoo", true),
      arguments("(START)?\\w+(?(1)foo|bar)", null, "foobar", true),
      arguments("(START)?\\w+(?(1)foo|bar)", null, "STARTfoo", false),
      arguments("((?(R1)a|b))", null, "b", true),
      arguments("((?(R1)a|b))", null, "a", false),
      arguments("(?(VERSION>=10.4)a|b)", null, "a", true),
      arguments("(?(VERSION>=10.4)a|b)", null, "b", false),
      // Char Unicode
      arguments("\\N{U+0041}", null, "A", true),
      arguments("(sens|respons)e and \\g{1}ibility", null, "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibility", null, "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibilitys", null, "response and responsibility", false),
      // Named groups
      arguments("(?<test>abc)", null, "abc", true),
      arguments("(?(DEFINE)(?<test>a))(?&test)", null, "a", true),
      arguments("(?(DEFINE)(?<test>a))(?&test)", null, "b", false),
      // PERL5
      // inline comments
      arguments("foo(?#comment)bar", null, "foobar", true),
      arguments("foo(?#comment)bar", null, "baz", false),
      // JAVA
      // Named Groups
      arguments("(?<test>abc)", null, "abc", true)
  );

  private static final List<Arguments> invalidPatterns = List.of(
      // Java

      // PERL5
      // Code
      arguments("R(?{1 == 1})", null, "R"),
      // Char name
      arguments("\\N{LATIN CAPITAL LETTER A}", null, "A")
  );

  // compiles but produces incorrect result
  private static final List<Arguments> falsePositives = List.of(
      // Java
      // Character Union
      arguments("[0[^\\W\\d]]", null, "0", true)
  );
}
