package com.jinloes.coding_practice;

import java.util.ArrayList;
import java.util.List;

public class GenerateParenthesis {
  public List<String> generateParenthesis(int n) {
    List<String> result = new ArrayList<>();
    StringBuilder current = new StringBuilder();
    doGenerate(result, current, 0, 0, n);

    return result;
  }

  private void doGenerate(List<String> result, StringBuilder current, int numLParen, int numRParen, int n) {
    if (numLParen == numRParen && numLParen == n) {
      result.add(current.toString());
      return;
    }
    if (numLParen < n) {
      current.append("(");
      doGenerate(result, current, numLParen + 1, numRParen, n);
      current.deleteCharAt(current.length() - 1);
    }

    if (numRParen < numLParen) {
      current.append(")");
      doGenerate(result, current, numLParen, numRParen + 1, n);
      current.deleteCharAt(current.length() - 1);
    }
  }
}
