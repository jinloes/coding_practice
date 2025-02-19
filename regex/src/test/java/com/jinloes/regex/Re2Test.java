package com.jinloes.regex;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.re2j.Matcher;
import com.google.re2j.Pattern;
import java.io.IOException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

public class Re2Test implements RegexRunner {

  @ParameterizedTest
  @ArgumentsSource(ValidPCRE2TestPatterns.class)
  public void testValidPatterns(String regex, String toTest, boolean expected) {
    Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(toTest);
    assertThat(matcher.matches())
        .isEqualTo(expected);
  }

  @Test
  @Disabled
  public void testMatchesFromFile() throws IOException {
    runRegexes(null, (regex, data) -> {
      Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
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
      Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
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
}
