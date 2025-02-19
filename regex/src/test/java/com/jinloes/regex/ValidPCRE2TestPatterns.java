package com.jinloes.regex;

import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.EnumSet;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.pcre4j.Pcre2CompileOption;

public class ValidPCRE2TestPatterns implements ArgumentsProvider {
  Stream<Arguments> VALID_PATTERNS = Stream.of(
      arguments("(?i)foo", "Foo", true),
      arguments("foo", "foo", true),
      arguments("foo.*?", "foo", true),
      arguments("foo.*?", "bar", false),
      arguments("(?i)foo.*?", "Foo", true),
      // Conditional
      arguments("(START)?\\w+(?(1)foo|bar)", "STARTabcfoo", true),
      arguments("(START)?\\w+(?(1)foo|bar)", "foobar", true),
      arguments("(START)?\\w+(?(1)foo|bar)", "STARTfoo", false),
      arguments("((?(R1)a|b))", "b", true),
      arguments("((?(R1)a|b))", "a", false),
      arguments("(?(VERSION>=10.4)a|b)", "a", true),
      arguments("(?(VERSION>=10.4)a|b)", "b", false),
      // Char Unicode
      arguments("\\N{U+0041}", "A", true),
      arguments("(sens|respons)e and \\g{1}ibility", "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibility", "response and responsibility", true),
      arguments("(sens|respons)e and \\1ibilitys", "response and responsibility", false),
      arguments("\\p{Lu}", "A", true),
      arguments("\\p{han}", "漢", true),
      // Named groups
      arguments("(?<test>abc)", "abc", true),
      arguments("(?(DEFINE)(?<test>a))(?&test)", "a", true),
      arguments("(?(DEFINE)(?<test>a))(?&test)", "b", false),
      // PERL5
      // inline comments
      arguments("foo(?#comment)bar", "foobar", true),
      arguments("foo(?#comment)bar", "baz", false),
      // JAVA
      // Named Groups
      arguments("(?<test>abc)", "abc", true)
  );

  @Override
  public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
    return VALID_PATTERNS;
  }
}
