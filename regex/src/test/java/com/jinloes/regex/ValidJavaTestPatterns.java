package com.jinloes.regex;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class ValidJavaTestPatterns implements ArgumentsProvider {
  private static final Stream<Arguments> PATTERNS = Stream.of(
      // Named groups
      arguments("(?<test>abc)", "abc", true),
      //Greedy
      arguments("abc{1}", "abc", true),
      arguments("abc{2}", "abc", false),
      arguments("abc{1}", "abcabc", false),
      arguments("rub(?:y|le)", "ruby", true),
      arguments("rub(?:y|le)", "ruble", true),
      arguments("rub(?:y|le)", "rub", false),
      // Backreference
      arguments("(sens|respons)e and \\1ibility", "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibilitys", "response and responsibility", false),
      // Character Union
      arguments("[0[^\\W\\d]]", "0", true),
      arguments("[0[^\\W\\d]]", "d", true),
      arguments("[0[^\\W\\d]]", "1", false),
      arguments("\\N{LATIN CAPITAL LETTER A}", "A", true),
      arguments("\\N{LATIN CAPITAL LETTER A}", "B", false),
      arguments("{1,3}[A-F]", "A", true),
      arguments("\\p{InCJKUnifiedIdeographs}", "漢", true)
  );

  @Override public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
    return PATTERNS;
  }
}
