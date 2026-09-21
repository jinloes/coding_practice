package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

class CorrectnessTest {
    @Test
    void handlesAnEmptyWord() {
        assertIndex("", -1);
    }

    @Test
    void handlesASingleCharacter() {
        assertIndex("q", 0);
    }

    @Test
    void handlesAPairOfTheSameCharacter() {
        assertIndex("zz", -1);
    }

    @Test
    void findsAUniqueCharacterAtTheEnd() {
        assertIndex("aabbc", 4);
    }

    @Test
    void ignoresARepeatThatIsFarAway() {
        assertIndex("abcdefghijklmnopqrstuvwxyza", 1);
    }

    @Test
    void countsCharactersThatAppearThreeTimes() {
        assertIndex("aaabcb", 4);
    }

    @Test
    void findsTheLeftmostOfSeveralUniqueCharacters() {
        assertIndex("ddxyz", 2);
    }

    @Test
    void handlesALongWordWithOneUniqueCharacter() {
        assertIndex("ab".repeat(400) + "c" + "ab".repeat(400), 800);
    }

    private static void assertIndex(String word, int expected) {
        assertThat(index(word)).as("%s must return %d", call(word), expected).isEqualTo(expected);
    }

    private static int index(String word) {
        try {
            return Solution.firstUniqueIndex(word);
        } catch (Throwable thrown) {
            return fail("%s threw %s".formatted(call(word), thrown), thrown);
        }
    }

    private static String call(String word) {
        String shown = word.length() <= 40 ? word : word.substring(0, 40) + "... (" + word.length() + " letters)";
        return "firstUniqueIndex(\"%s\")".formatted(shown);
    }
}
