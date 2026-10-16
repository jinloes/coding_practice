package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void zeroPairsGivesTheEmptyString() {
        assertThat(Solution.generate(0)).as("generate(0)").containsExactly("");
    }

    @Test
    void negativePairsAreRejected() {
        assertThatThrownBy(() -> Solution.generate(-1)).as("generate(-1)")
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void fourPairsGivesFourteenValidStrings() {
        check(4, 14);
    }

    @Test
    void fivePairsGivesFortyTwoValidStrings() {
        check(5, 42);
    }

    @Test
    void eightPairsGivesCatalanManyValidStrings() {
        check(8, 1430);
    }

    @Test
    void tenPairsGivesCatalanManyValidStrings() {
        check(10, 16_796);
    }

    @Test
    void includesTheExtremeShapes() {
        List<String> result = Solution.generate(6);
        assertThat(result).as("generate(6) contains fully nested").contains("(((((())))))");
        assertThat(result).as("generate(6) contains fully flat").contains("()()()()()()");
    }

    @Test
    void doesNotShareStateBetweenCalls() {
        assertThat(Solution.generate(3)).as("generate(3) first call").hasSize(5);
        assertThat(Solution.generate(3)).as("generate(3) called again").hasSize(5);
        assertThat(Solution.generate(2)).as("generate(2) after generate(3)").containsExactlyInAnyOrder("(())", "()()");
    }

    private static void check(int pairs, int count) {
        String call = "generate(%d)".formatted(pairs);
        List<String> result = Solution.generate(pairs);
        assertThat(result).as(call + " size").hasSize(count);
        assertThat(result).as(call + " must not repeat a string").doesNotHaveDuplicates();
        for (String text : result) {
            if (!wellFormed(text, 2 * pairs)) {
                assertThat(text).as(call + " string must be well formed").isNull();
            }
        }
    }

    private static boolean wellFormed(String text, int length) {
        if (text.length() != length) {
            return false;
        }
        int open = 0;
        for (int i = 0; i < text.length(); i++) {
            open += text.charAt(i) == '(' ? 1 : text.charAt(i) == ')' ? -1 : 100;
            if (open < 0 || open > length) {
                return false;
            }
        }
        return open == 0;
    }
    }

