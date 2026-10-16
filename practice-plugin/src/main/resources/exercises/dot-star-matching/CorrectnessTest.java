package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyPatternMatchesOnlyEmptyText() {
        check("", "", true);
    }

    @Test
    void emptyPatternRejectsLetters() {
        check("a", "", false);
    }

    @Test
    void emptyTextRejectsLetterPattern() {
        check("", "a", false);
    }

    @Test
    void starCanBeUsedZeroTimes() {
        check("aaa", "ab*a*c*a", true);
    }

    @Test
    void dotStarMatchesEverything() {
        check("ab", ".*", true);
    }

    @Test
    void dotStarThenLetter() {
        check("abcd", ".*d", true);
    }

    @Test
    void dotStarThenMissingLetter() {
        check("abcd", ".*e", false);
    }

    @Test
    void mississippiNeedsBothSAndIStars() {
        check("mississippi", "mis*is*ip*.", true);
    }

    @Test
    void mississippiFailsWithoutExtraI() {
        check("mississippi", "mis*is*p*.", false);
    }

    @Test
    void severalStarsShareTheLetters() {
        check("aaaa", "a*a*a*a", true);
    }

    @Test
    void mustConsumeWholeText() {
        check("abc", "ab", false);
    }

    @Test
    void longTextAgainstTwoStars() {
        check("a".repeat(300), "a*a*b*", true);
    }

    @Test
    void longTextWithImpossibleTail() {
        check("a".repeat(300) , "a*a*b", false);
    }

    private static void check(String text, String pattern, boolean expected) {
        String call = "matches(\"%s\", \"%s\")".formatted(text, pattern);
        assertThat(Solution.matches(text, pattern)).as(call).isEqualTo(expected);
    }
}
