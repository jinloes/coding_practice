package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void ordersLettersFromFirstDifferences() {
        String[] words = {"zy", "zx", "yx"};
        assertLegalOrder(words, Solution.alienOrder(words));
    }

    @Test
    void rejectsALongWordBeforeItsOwnPrefix() {
        assertThat(Solution.alienOrder(new String[]{"abc", "ab"})).isEmpty();
    }

    @Test
    void singleWordYieldsItsLettersInAnyOrder() {
        String[] words = {"dog"};
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
