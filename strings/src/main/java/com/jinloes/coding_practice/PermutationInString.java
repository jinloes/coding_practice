package com.jinloes.coding_practice;

import java.util.HashMap;
import java.util.Map;

public class PermutationInString {
  public boolean checkInclusion(String s1, String s2) {
    if (s1.length() > s2.length()) {
      return false;
    }
    Map<Character, Integer> s1Count = new HashMap<>();
    Map<Character, Integer> s2Count = new HashMap<>();
    countChars(s1, s1Count);

    int matches = s1Count.size();
    int currentMatches = 0;

    int start = 0;
    int end = 0;

    while (end < s2.length()) {
      char c = s2.charAt(end);
      int newCount = s2Count.getOrDefault(c, 0) + 1;
      s2Count.put(c, newCount);
      if (s1Count.containsKey(c) && s1Count.get(c) == newCount) {
        currentMatches++;
      } else if (s1Count.containsKey(c) && s1Count.get(c) == newCount - 1) {
        currentMatches--;
      }

      if (end - start >= s1.length()) {
        char startChar = s2.charAt(start);
        newCount = s2Count.getOrDefault(startChar, 0) - 1;
        s2Count.put(startChar, newCount);
        if (s1Count.containsKey(startChar) && s1Count.get(startChar) == newCount) {
          currentMatches++;
        } else if (s1Count.containsKey(startChar) && s1Count.get(startChar) == newCount + 1) {
          currentMatches--;
        }
        start++;
      }
      end++;

      if (matches == currentMatches) {
        return true;
      }
    }
    return false;
  }

  private void countChars(String str, Map<Character, Integer> map) {
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      map.put(c, map.getOrDefault(c, 0) + 1);
    }
  }
}