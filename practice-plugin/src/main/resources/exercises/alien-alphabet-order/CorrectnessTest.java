package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void noWordsGivesEmptyOrder() {
        assertThat(Solution.alienOrder(new String[0])).isEmpty();
    }

    @Test
    void twoSingleLetterWordsFixTheirOrder() {
        assertThat(Solution.alienOrder(new String[]{"x", "y"})).isEqualTo("xy");
        assertThat(Solution.alienOrder(new String[]{"y", "x"})).isEqualTo("yx");
    }

    @Test
    void duplicateWordsAreConsistent() {
        String[] words = {"ab", "ab", "ac"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    @Test
    void shorterPrefixFirstIsAllowed() {
        String[] words = {"app", "apple", "apt"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    @Test
    void prefixAfterLongerWordIsInconsistent() {
        assertThat(Solution.alienOrder(new String[]{"apple", "app"})).isEmpty();
        assertThat(Solution.alienOrder(new String[]{"ab", "abc", "ab"})).isEmpty();
    }

    @Test
    void lettersWithoutConstraintsStillAppear() {
        String[] words = {"ca", "cb", "d"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    @Test
    void transitiveConstraintsAreHonored() {
        String[] words = {"ba", "bc", "ca", "cb"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    @Test
    void twoLetterCycleIsInconsistent() {
        assertThat(Solution.alienOrder(new String[]{"ab", "ba", "ab"})).isEmpty();
    }

    @Test
    void threeLetterCycleIsInconsistent() {
        assertThat(Solution.alienOrder(new String[]{"a", "b", "c", "a"})).isEmpty();
    }

    @Test
    void cycleFoundAfterValidWordsIsInconsistent() {
        assertThat(Solution.alienOrder(new String[]{"ab", "ac", "ca", "cb", "bb", "ba"})).isEmpty();
    }

    @Test
    void fullAlphabetChainIsRecoveredExactly() {
        String[] words = new String[26];
        for (int i = 0; i < 26; i++) {
            words[i] = String.valueOf((char) ('z' - i));
        }
        assertThat(Solution.alienOrder(words)).isEqualTo("zyxwvutsrqponmlkjihgfedcba");
    }

    @Test
    void repeatedConstraintsAreHandled() {
        String[] words = {"aab", "aac", "abb", "abc", "bbb"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    private static void assertLegalOrder(String[] words, String order) {
        Set<Character> letters = new HashSet<>();
        for (String word : words) {
            for (char letter : word.toCharArray()) {
                letters.add(letter);
            }
        }
        assertThat(order).as("order for %s", Arrays.toString(words)).hasSize(letters.size());
        Set<Character> used = new HashSet<>();
        for (char letter : order.toCharArray()) {
            assertThat(letters).as("letter %s appears in the words", letter).contains(letter);
            assertThat(used.add(letter)).as("letter %s listed once", letter).isTrue();
        }
        for (int i = 1; i < words.length; i++) {
            String first = words[i - 1];
            String second = words[i];
            int j = 0;
            while (j < first.length() && j < second.length() && first.charAt(j) == second.charAt(j)) {
                j++;
            }
            if (j < first.length() && j < second.length()) {
                assertThat(order.indexOf(first.charAt(j)))
                        .as("%s must come before %s in %s", first.charAt(j), second.charAt(j), order)
                        .isLessThan(order.indexOf(second.charAt(j)));
            }
        }
    }
}
