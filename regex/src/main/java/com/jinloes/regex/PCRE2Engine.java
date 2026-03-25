package com.jinloes.regex;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2JitCode;
import org.pcre4j.Pcre2MatchContext;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.option.Pcre2CompileOption;
import org.pcre4j.option.Pcre2JitOption;
import org.pcre4j.option.Pcre2MatchOption;

/**
 * Low-level PCRE2 regex engine using the pcre4j JNA backend.
 * Compiles patterns with UTF and MULTILINE options by default.
 */
public class PCRE2Engine {

  /**
   * Tests whether {@code toTest} fully matches {@code regex}.
   * Uses ANCHORED + ENDANCHORED match options so the entire string must be consumed.
   *
   * @param toTest  the input string to match against
   * @param options compile options; defaults to UTF + MULTILINE if null
   * @param regex   the PCRE2 pattern
   * @param useJit  whether to enable JIT compilation (controlled by {@code pcre2.regex.jit} system property)
   * @return positive number of captures on match, negative PCRE2 error code on no match
   */
  public int test(String toTest, EnumSet<Pcre2CompileOption> options, String regex, boolean useJit) {
    var compileOptions = Objects.requireNonNullElse(options, EnumSet.of(Pcre2CompileOption.UTF, Pcre2CompileOption.MULTILINE));

    if (useJit && isJitAllowed()) {
      // JIT requires ANCHORED + ENDANCHORED at compile time
      compileOptions = EnumSet.copyOf(compileOptions);
      compileOptions.add(Pcre2CompileOption.ANCHORED);
      compileOptions.add(Pcre2CompileOption.ENDANCHORED);
    }

    var pcre2Code = compile(regex, compileOptions, useJit);
    var matchData = new Pcre2MatchData(pcre2Code);
    var matchContext = new Pcre2MatchContext(pcre2Code.api(), null);
    return pcre2Code.match(toTest, 0,
        EnumSet.of(Pcre2MatchOption.ANCHORED, Pcre2MatchOption.ENDANCHORED), matchData, matchContext);
  }

  /**
   * Finds all non-overlapping substrings in {@code input} that match {@code regex}.
   * Equivalent to {@code findMatches(input, regex, false)}.
   *
   * @param input the string to search
   * @param regex the PCRE2 pattern
   * @return list of matched substrings in order of appearance; empty if no matches
   */
  public List<String> findMatches(String input, String regex) {
    return findMatches(input, regex, false);
  }

  /**
   * Finds all non-overlapping substrings in {@code input} that match {@code regex}.
   *
   * <p>The input is encoded as UTF-8 before matching. PCRE2 returns byte offsets in the
   * ovector, so after each match the byte end offset is decoded back to a Java char offset
   * to advance the search position correctly for multi-byte characters.
   *
   * @param input  the string to search
   * @param regex  the PCRE2 pattern
   * @param useJit whether to enable JIT compilation (controlled by {@code pcre2.regex.jit} system property)
   * @return list of matched substrings in order of appearance; empty if no matches
   */
  public List<String> findMatches(String input, String regex, boolean useJit) {
    var options = EnumSet.of(Pcre2CompileOption.UTF, Pcre2CompileOption.MULTILINE);
    var pcre2Code = compile(regex, options, useJit);
    var matchData = new Pcre2MatchData(pcre2Code);
    var matchContext = new Pcre2MatchContext(pcre2Code.api(), null);

    // Encode once; ovector positions are byte offsets into t his array
    byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);
    List<String> matches = new ArrayList<>();
    int charOffset = 0;
    while (pcre2Code.match(input, charOffset, EnumSet.noneOf(Pcre2MatchOption.class), matchData, matchContext) > 0) {
      long[] ovector = matchData.ovector();
      int start = (int) ovector[0];
      int end = (int) ovector[1];
      matches.add(new String(inputBytes, start, end - start, StandardCharsets.UTF_8));
      // Convert byte end offset back to char offset for the next match call
      charOffset = new String(inputBytes, 0, end, StandardCharsets.UTF_8).length();
    }
    return matches;
  }

  private static boolean isJitAllowed() {
    return Boolean.parseBoolean(System.getProperty("pcre2.regex.jit", "true"));
  }

  private static Pcre2Code compile(String regex, EnumSet<Pcre2CompileOption> options, boolean useJit) {
    if (useJit && isJitAllowed()) {
      return new Pcre2JitCode(regex, options, EnumSet.of(Pcre2JitOption.COMPLETE), null);
    }
    return new Pcre2Code(regex, options);
  }
}