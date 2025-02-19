package com.jinloes.regex;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface PCRE2Demo extends Library {
  PCRE2Demo INSTANCE = Native.load("pcre2demo-lib", PCRE2Demo.class);

  MatchResult.ByValue run_regex(String regex, String data);
}
