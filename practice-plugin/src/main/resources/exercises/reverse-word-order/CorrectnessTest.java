package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndBlankInputReturnEmpty() { assertThat(Solution.reverseWords("")).isEmpty(); assertThat(Solution.reverseWords("    ")).isEmpty(); }
    @Test
    void punctuationStaysWithItsWord() { assertThat(Solution.reverseWords("red, blue! green?")).isEqualTo("green? blue! red,"); }
    @Test
    void tabsArePartOfWordsUnderSpaceDelimiter() { assertThat(Solution.reverseWords("a	b c")).isEqualTo("c a	b"); }
    @Test
    void longSentenceReversesAllWords() { String input = String.join(" ", java.util.stream.IntStream.range(0, 1000).mapToObj(i -> "w" + i).toArray(String[]::new)); assertThat(Solution.reverseWords(input)).startsWith("w999 w998 w997").endsWith("w2 w1 w0"); }
}
