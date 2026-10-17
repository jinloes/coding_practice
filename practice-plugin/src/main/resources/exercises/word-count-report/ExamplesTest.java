package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void countsRepeatedWordsIgnoringCaseAndPunctuation() { assertThat(Solution.countWords("Red fish, red fish!")).containsExactly("2 red", "2 fish"); }
    @Test
    void sortsByLengthThenWord() { assertThat(Solution.countWords("One two two three.")).containsExactly("1 one", "2 two", "1 three"); }
    @Test
    void punctuationOnlyProducesNoRows() { assertThat(Solution.countWords(" ... ")).isEmpty(); }
}
