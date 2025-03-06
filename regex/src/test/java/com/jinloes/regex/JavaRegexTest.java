package com.jinloes.regex;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class JavaRegexTest implements RegexRunner {
  private PCRE2Engine pcre2Engine;

  @BeforeEach
  public void setUp() {
    pcre2Engine = new PCRE2Engine();
  }

  @DisplayName("Java Regex Tests")
  @ParameterizedTest
  @ArgumentsSource(ValidJavaTestPatterns.class)
  public void test(String pattern, String toTest, boolean expected) {
    var matcher = Pattern.compile(pattern, Pattern.MULTILINE)
        .matcher(toTest);
    assertThat(matcher.matches())
        .isEqualTo(expected);
  }

  @DisplayName("Java regex invalid patterns")
  @ParameterizedTest
  @ArgumentsSource(InvalidJavaPcre2Patterns.class)
  public void testInvalidPatterns(String pattern, String toTest) {
    assertThatThrownBy(() ->
        Pattern.compile(pattern, Pattern.MULTILINE)
            .matcher(toTest)
            .matches())
        .isInstanceOf(PatternSyntaxException.class);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record Regex(@JsonProperty("regex") String regex) {

  }

  @Test
  public void testFile() throws IOException {
    Path allRegexPath =
        Path.of(System.getenv("INPUT_REGEX_FILE"));
    Path output = Path.of(System.getenv("OUTPUT_REGEX_FILE"));
    FileUtils.deleteQuietly(output.toFile());
    Map<String, List<Regex>> allRegexes = new ObjectMapper().readValue(allRegexPath.toFile(), new TypeReference<>() {
    });
    List<List<Pair<String, String>>> regexes = allRegexes.values().stream()
        .flatMap(Collection::stream)
        .map(regex -> regex.regex)
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

    List<Pair<String, String>> valid = regexes.get(0);
    List<Pair<String, String>> invalid = regexes.get(1);

    String invalidRegexes = invalid.stream()
        .map(Pair::getLeft)
        .collect(Collectors.joining("\n"));

    System.out.println(invalidRegexes);
    System.out.printf("Total: %s, Valid: %s Failed %s", valid.size() + invalid.size(), valid.size(), invalid.size());
    FileUtils.writeLines(output.toFile(), valid.stream()
        .map(Pair::getLeft)
        .collect(Collectors.toList()));
  }

  @Test
  @Disabled
  public void testMatchesFromFile() throws IOException {
    runRegexes(null, (regex, data) -> {
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(data);
      int numMatches = 0;
      if (matcher.matches()) {
        numMatches = 1;
      }
      return MatchResult.builder()
          .numMatches(numMatches)
          .build();
    });
  }

  @Test
  @Disabled
  public void testLoopMatchesFromFile() throws IOException {
    runRegexes(null, (regex, data) -> {
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(data);
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
  public void testCompareJavaPCRE2Matches() throws IOException {
    runRegexes(null, (regex, data) -> {
      Pattern pattern = Pattern.compile(regex);
      Matcher matcher = pattern.matcher(data);

      CompletableFuture<List<String>> javaFuture = CompletableFuture.supplyAsync(() -> {
        List<String> javaMatches = new ArrayList<>();
        while (matcher.find()) {
          String match = matcher.group();
          javaMatches.add(match);
        }
        return javaMatches;
      });


      CompletableFuture<List<String>> pcre2Future = CompletableFuture.supplyAsync(() -> {
        MatchResult result = pcre2Engine.iterateMatches(data, regex, false);
        return Arrays.asList(result.matches);
      });

      List<String> javaMatches = javaFuture.join();
      List<String> pcre2Matches = pcre2Future.join();

      if (javaMatches.size() != pcre2Matches.size()) {
        System.out.printf("Regex %s different amount of matches Java: %d PCRE2: %d\n", javaMatches.size(),
            pcre2Matches.size());
      } else if (!javaMatches.isEmpty()) {
        for (int i = 0; i < javaMatches.size(); i++) {
          String javaMatch = javaMatches.get(i);
          String pcre2Match = pcre2Matches.get(i);
          if (!Objects.equals(javaMatch, pcre2Match)) {
            System.out.printf("Regex %s\n Java: %s PCRE: %s\n", regex, javaMatch, pcre2Match);
          }
        }
      }

      return MatchResult.builder()
          .numMatches(javaMatches.size())
          .build();
    });
  }
}
