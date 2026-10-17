package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndWhitespaceReturnEmpty() { assertThat(Solution.countWords("")).isEmpty(); assertThat(Solution.countWords("  	  ")).isEmpty(); }
    @Test
    void trimsPunctuationOnBothEndsButKeepsInteriorCharacters() { assertThat(Solution.countWords("'can't' can't end-to-end.")).containsExactly("2 can't", "1 end-to-end"); }
    @Test
    void numbersAreWordsToo() { assertThat(Solution.countWords("7 007 7!")).containsExactly("2 7", "1 007"); }
    @Test
    void largerTextHasStableOrdering() { StringBuilder text = new StringBuilder(); for (int i = 0; i < 2000; i++) text.append(i % 3 == 0 ? "alpha " : i % 3 == 1 ? "beta! " : "Gamma, "); assertThat(Solution.countWords(text.toString())).containsExactly("667 beta", "667 alpha", "666 gamma"); }
}
