package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void distributesExtraSpacesLeftToRightAndLeftJustifiesLastLine() {
        String[] words = {"Ship", "clean", "code", "to", "teams", "every", "day."};
        assertThat(solution.fullJustify(words, 14)).containsExactly(
                "Ship     clean",
                "code  to teams",
                "every day.    ");
    }

    @Test
    void leftJustifiesSingleWordMiddleLineAndLastLine() {
        String[] words = {"Fast", "builds", "require", "instrumentation", "and", "tests"};
        assertThat(solution.fullJustify(words, 16)).containsExactly(
                "Fast      builds",
                "require         ",
                "instrumentation ",
                "and tests       ");
    }

    @Test
    void padsSingleLastLineOnTheRight() {
        assertThat(solution.fullJustify(new String[]{"Code", "well"}, 10))
                .containsExactly("Code well ");
    }
}
