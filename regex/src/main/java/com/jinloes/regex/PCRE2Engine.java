package com.jinloes.regex;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.pcre4j.Pcre2Code;
import org.pcre4j.Pcre2CompileContext;
import org.pcre4j.Pcre2CompileOption;
import org.pcre4j.Pcre2JitCode;
import org.pcre4j.Pcre2MatchContext;
import org.pcre4j.Pcre2MatchData;
import org.pcre4j.Pcre2MatchOption;
import org.pcre4j.Pcre4j;
import org.pcre4j.Pcre4jUtils;
import org.pcre4j.api.IPcre2;
import org.pcre4j.jna.Pcre2;

public class PCRE2Engine {
  private IPcre2 api;
  private Pcre2 pcre2;

  public PCRE2Engine() {
    pcre2 = new Pcre2("libpcre2-8.dylib", "_8");
    Pcre4j.setup(pcre2);
    api = Pcre4j.api();
  }

  public int test(String toTest, EnumSet<Pcre2CompileOption> options, String regex, boolean useJit) {
    options = Objects.requireNonNullElse(options, EnumSet.of(Pcre2CompileOption.UTF, Pcre2CompileOption.MULTILINE));
    var compileContext = new Pcre2CompileContext(api, null);
    //compileContext.setNewline(Pcre2Newline.ANY);

    final var isJitAllowed = Boolean.parseBoolean(System.getProperty("pcre2.regex.jit", "true"));
    Pcre2Code pcre2Code;
    if (useJit && isJitAllowed) {
      final var matchingCompileOptions = EnumSet.copyOf(options);
      matchingCompileOptions.add(Pcre2CompileOption.ANCHORED);
      matchingCompileOptions.add(Pcre2CompileOption.ENDANCHORED);
      pcre2Code = new Pcre2JitCode(api, regex, matchingCompileOptions, null, compileContext);
    } else {
      pcre2Code = new Pcre2Code(api, regex, options, compileContext);
    }

    var matchData = new Pcre2MatchData(pcre2Code);
    var matchContext = new Pcre2MatchContext(api, null);
    int numMatches = pcre2Code.match(toTest, 0,
        EnumSet.of(Pcre2MatchOption.ANCHORED, Pcre2MatchOption.ENDANCHORED), matchData, matchContext);
    return numMatches;
  }

  public int matches(String toTest, String regex) {
    final var pcre2 = new Pcre2();

    final var errorcode = new int[1];
    final var erroroffset = new long[1];
    final var code = pcre2.compile(regex, 0, errorcode, erroroffset, 0);
    if (code == 0) {
      throw new RuntimeException(
          "PCRE2 compilation failed with error code " + errorcode[0] + " at offset " + erroroffset[0]
      );
    }

    long matchData = api.matchDataCreateFromPattern(code, 0);
    long matchContext = api.matchContextCreate(0);

    int matches = pcre2.match(code, toTest, 0, 0, matchData, matchContext);

    api.matchDataFree(matchData);
    api.matchContextFree(matchContext);
    api.codeFree(code);

    return matches;
  }

  public static final int JIT_COMPLETE = Integer.decode("0x00000001");

  public MatchResult iterateMatches(String toTest, String regex, boolean useJit) {
    final var pcre2 = new Pcre2();

    final var errorcode = new int[1];
    final var erroroffset = new long[1];
    var code = pcre2.compile(regex, 0, errorcode, erroroffset, 0);
    long matchData = api.matchDataCreateFromPattern(code, 0);
    long matchContext = api.matchContextCreate(0);
    long jitStack = 0;

    if (useJit) {
      code = api.jitCompile(code, JIT_COMPLETE);
      jitStack = api.jitStackCreate(5 * 1024 * 1024, 1024 * 1024 * 100, 0);
      api.jitStackAssign(matchContext, 0, jitStack);
    }

    if (code == 0) {
      throw new RuntimeException(
          "PCRE2 compilation failed with error code " + errorcode[0] + " at offset " + erroroffset[0]
      );
    }


    int hasMatch;
    int count = 0;
    long offset = 0;
    List<String> matches = new ArrayList<>();
    while ((hasMatch = doMatch(code, toTest, (int) offset, matchData, matchContext, useJit)) > 0) {
      count++;
      var ovector = new long[api.getOvectorCount(matchData) * 2];
      api.getOvector(matchData, ovector);
      matches.add(getMatchGroups(toTest, ovector)[0]);

      offset = ovector[1];
    }
    api.matchDataFree(matchData);
    api.matchContextFree(matchContext);
    api.codeFree(code);
    if (useJit && jitStack != 0) {
      api.jitStackFree(jitStack);
    }

    return MatchResult.builder()
        .numMatches(count)
        .matches(matches.toArray(new String[matches.size()]))
        .build();

  }

  private String[] getMatchGroups(String subject, long[] ovector) {
    final var stringIndices = Pcre4jUtils.convertOvectorToStringIndices(subject, ovector);

    final var matchGroupsCount = ovector.length / 2;
    final var matchGroups = new String[matchGroupsCount];
    for (var matchIndex = 0; matchIndex < matchGroupsCount; matchIndex++) {
      matchGroups[matchIndex] = subject.substring(
          stringIndices[matchIndex * 2],
          stringIndices[matchIndex * 2 + 1]
      );
    }
    return matchGroups;
  }

  private int doMatch(long code, String toTest, int offset, long matchData, long matchContext, boolean useJit) {
    if (useJit) {
      return api.jitMatch(code, toTest, offset, 0, matchData, matchContext);
    } else {
      return api.match(code, toTest, offset, 0, matchData, matchContext);
    }
  }

  public int runRegex(String regex, String data) {
    MatchResult.ByValue matchResult = PCRE2Demo.INSTANCE.run_regex(regex, data);
    return matchResult.numMatches;
  }
}
