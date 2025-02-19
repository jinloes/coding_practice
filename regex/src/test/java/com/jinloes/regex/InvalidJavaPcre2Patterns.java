package com.jinloes.regex;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class InvalidJavaPcre2Patterns implements ArgumentsProvider {
  private static final Stream<Arguments> PATTERNS = Stream.of(
      //PERL5
      // Code
      arguments("R(?{1=1})", "R"),
      arguments("R(??{1=1})", "R"),
      //PCRE
      // Comment
      arguments("R(?#comment)", "R"),
      // Backreference
      arguments("(sens|respons)e and \\g{1}ibility", "response and responsibility"),
      arguments("(sens|respons)e and \\g1ibility", "response and responsibility"),
      // Conditional
      arguments("(START)?\\w+(?(1)foo|bar)", "STARTabcfoo"),
      arguments("((?(R1)a|b))", "b"),
      arguments("(?(VERSION>=10.4)a|b)", "a"),
      // Char Unicode
      arguments("\\N{U+0041}", "A"),
      // Named groups
      arguments("(?(DEFINE)(?<test>a))(?&test)", "a")
  );

  @Override public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return PATTERNS;
  }
}
