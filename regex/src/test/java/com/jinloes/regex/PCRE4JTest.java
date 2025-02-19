package com.jinloes.regex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.FieldSource;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.api.IPcre2;
import org.pcre4j.regex.Matcher;
import org.pcre4j.regex.Pattern;

public class PCRE4JTest implements RegexRunner {
  private IPcre2 api;

  private PCRE2Engine pcre2Engine;

  @BeforeAll
  public static void setup() {
    //Pcre4j.setup(new Pcre2("libpcre2-8.dylib", "_8"));
  }

  @BeforeEach
  public void setUp() {
    //api = Pcre4j.api();
    pcre2Engine = new PCRE2Engine();
  }

  @DisplayName("Pcre4j valid patterns")
  @ParameterizedTest
  @ArgumentsSource(ValidPCRE2TestPatterns.class)
  public void testValidPatterns(String pattern, String toTest, boolean expected) {
    Matcher matcher = Pattern.compile(pattern, Pattern.MULTILINE)
        .matcher(toTest);
    matcher.matches();
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
  @ArgumentsSource(ValidPCRE2TestPatterns.class)
  public void testLowLevel(String regex, String toTest, boolean expected) {
    int numMatches = pcre2Engine.matches(toTest, regex);
    /*if (expected) {
      int start = Pcre4jUtils.convertOvectorToStringIndices(toTest, toTest.getBytes(), matchData.ovector())[0];
      int end = Pcre4jUtils.convertOvectorToStringIndices(toTest, toTest.getBytes(), matchData.ovector())[1];
      var groups = Pcre4jUtils.getMatchGroups(code, toTest, matchData);
      System.out.println(start);
      System.out.println(end);
    }*/

    assertThat(numMatches > 0)
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

  @Test
  public void testAnalyze() throws IOException {
    Path validRegexes = Path.of(System.getenv("OUTPUT_REGEX_FILE"));

    List<List<Pair<String, String>>> parsed = Files.lines(validRegexes)
        .map(regex -> {
          try {
            Pattern.compile(regex);
            return Pair.<String, String>of(regex, null);
          } catch (Exception e) {
            return Pair.of(regex, e.getMessage());
          }
        })
        .collect(Collectors.teeing(
            Collectors.filtering(result -> Objects.isNull(result.getRight()), Collectors.toList()),
            Collectors.filtering(result -> !Objects.isNull(result.getRight()), Collectors.toList()), List::of));

    List<Pair<String, String>> valid = parsed.get(0);
    List<Pair<String, String>> failed = parsed.get(1);

    String failedRegexes = failed.stream()
        .map(Object::toString)
        .collect(Collectors.joining("\n"));
    System.out.printf("Failed regexes: %s", failedRegexes);
    System.out.printf("Total %s\nvalid %s\nfailed %s%n", valid.size() + failed.size(), valid.size(),
        failed.size());
  }

  private static final List<Arguments> lowLevelPatterns = List.of(
      arguments("\\p{Lu}", null, "A", true),
      arguments("\\p{han}", null, "漢", true),
      arguments("foo", EnumSet.of(Pcre2CompileOption.UCP), "ABC", false),
      // PERL5
      // Ignore whitespace
      arguments("[d-eg-i3-7]", EnumSet.of(Pcre2CompileOption.UTF), "dg37", true),
      arguments("[d-e g-i 3-7]", EnumSet.of(Pcre2CompileOption.UTF, Pcre2CompileOption.EXTENDED_MORE), "dg37", true)
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


  @Test
  @Disabled
  public void testMatchesFromFile() throws IOException {
    runRegexes(null, (regex, data) -> {
      Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE)
          .matcher(data);
      int count = 0;
      if (matcher.matches()) {
        count = 1;
      }
      return MatchResult.builder()
          .numMatches(count)
          .build();
    });
  }

  @Test
  @Disabled
  public void testMatchesFromFileLowLevel() throws IOException {
    runRegexes(null, (regex, data) -> {
      int count = pcre2Engine.matches(data, regex);
      return MatchResult.builder()
          .numMatches(count)
          .build();
    });
  }

  @Test
  @Disabled
  public void testInterateMatchesFromFile() throws IOException {
    runRegexes(null, (regex, data) -> {
      Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE)
          .matcher(data);
      int count = 0;
      while (matcher.find()) {
        count++;
      }
      return MatchResult.builder()
          .numMatches(count)
          .build();
    });
  }

  @Test
  @Disabled
  public void testIterateMatchesFromFileLowLevel() throws IOException {
    runRegexes(null, (regex, data) -> {
      return pcre2Engine.iterateMatches(data, regex, true);q
    });
  }

  @Test
  public void testCountMatches() throws IOException {
    System.load("/usr/local/lib/pcre2demo-lib/libpcre2demo-lib.dylib");
    runRegexes(null, (regex, data) -> {
      int count = pcre2Engine.runRegex(regex, data);
      return MatchResult.builder()
          .numMatches(count)
          .build();
    });
  }
}
