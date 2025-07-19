package com.jinloes.coding_practice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class GenerateParenthesisTest {

  private GenerateParenthesis generateParenthesis;

  @BeforeEach
  void setUp() {
    generateParenthesis = new GenerateParenthesis();
  }

  @ParameterizedTest
  @MethodSource("generateParenthesisArgs")
  void generateParenthesis(int n, List<String> expected) {
    assertThat(generateParenthesis.generateParenthesis(n))
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> generateParenthesisArgs() {
    return Stream.of(
        Arguments.of(1, List.of("()")),
        Arguments.of(3, List.of("()()()", "(()())", "()(())", "(())()", "((()))")),
        Arguments.of(4,
            List.of("(((())))", "((()()))", "((())())", "((()))()", "(()(()))", "(()()())",
                "(()())()", "(())(())", "(())()()", "()((()))", "()(()())", "()(())()", "()()(())",
                "()()()()")),
        Arguments.of(5, List.of("((((()))))","(((()())))","(((())()))","(((()))())","(((())))()",
            "((()(())))","((()()()))","((()())())","((()()))()","((())(()))","((())()())","((())())()","((()))(())",
            "((()))()()","(()((())))","(()(()()))","(()(())())","(()(()))()","(()()(()))","(()()()())","(()()())()",
            "(()())(())","(()())()()","(())((()))","(())(()())","(())(())()","(())()(())","(())()()()","()(((())))",
            "()((()()))","()((())())","()((()))()","()(()(()))","()(()()())","()(()())()","()(())(())","()(())()()",
            "()()((()))","()()(()())","()()(())()","()()()(())","()()()()()"))
    );
  }
}