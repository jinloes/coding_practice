package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void tieUsesEarliestLeftBoundary() { assertThat(Solution.smallestCover(new String[]{"a", "b", "x", "a", "b"}, new String[]{"a", "b"})).containsExactly(0, 1); }
    @Test
    void singleKeywordCanReturnSingleElementRange() { assertThat(Solution.smallestCover(new String[]{"x", "target", "x"}, new String[]{"target"})).containsExactly(1, 1); }
    @Test
    void duplicateWordsInsideWindowAreHandled() { assertThat(Solution.smallestCover(new String[]{"a", "a", "b", "c", "a"}, new String[]{"a", "b", "c"})).containsExactly(1, 3); }
    @Test
    void keywordsMayAppearOnlyAtExtremes() { assertThat(Solution.smallestCover(new String[]{"start", "x", "x", "end"}, new String[]{"start", "end"})).containsExactly(0, 3); }
    @Test
    void handlesLargeWindow() { String[] words = new String[6000]; for (int i = 0; i < words.length; i++) words[i] = "filler"; words[1234] = "red"; words[3456] = "blue"; words[3457] = "red"; words[3458] = "green"; assertThat(Solution.smallestCover(words, new String[]{"red", "blue", "green"})).containsExactly(3456, 3458); }
}
