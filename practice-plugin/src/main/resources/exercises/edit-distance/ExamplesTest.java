package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void deletesOneLetter() {
        check("planet", "plant", 1);
    }

    @Test
    void deletesThenAppends() {
        check("flaw", "lawn", 2);
    }

    @Test
    void buildsFromEmpty() {
        check("", "abc", 3);
    }

    private static void check(String from, String to, int expected) {
        String call = "minEdits(\"%s\", \"%s\")".formatted(from, to);
        assertThat(Solution.minEdits(from, to)).as(call).isEqualTo(expected);
    }
}
