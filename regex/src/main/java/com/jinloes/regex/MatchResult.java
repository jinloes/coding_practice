package com.jinloes.regex;

import com.sun.jna.Structure;
import java.util.Set;
import lombok.Builder;

@Builder
@Structure.FieldOrder({"numMatches"})
public class MatchResult extends Structure {
  public static class ByValue extends MatchResult implements Structure.ByValue {
    public ByValue(int numMatches, String[] matches) {
      super(numMatches, matches);
    }
  }


  public int numMatches;
  public String[] matches;
}
