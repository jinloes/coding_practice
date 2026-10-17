package com.jinloes.practice;



import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.assertThat;



class CorrectnessTest {

    @Test

    void findsPatternStartingAtLaterRowAndColumn() {

        assertThat(Solution.containsPattern(new String[]{"00000", "01234", "05678", "09999"}, new String[]{"23", "67"}))

                .isTrue();

    }



    @Test

    void rejectsEmptyPattern() {

        assertThat(Solution.containsPattern(new String[]{"123"}, new String[0])).isFalse();

    }



    @Test

    void rejectsPatternLargerThanGrid() {

        assertThat(Solution.containsPattern(new String[]{"12"}, new String[]{"12", "34"})).isFalse();

    }



    @Test

    void requiresEveryPatternRowToMatch() {

        assertThat(Solution.containsPattern(new String[]{"111", "222", "333"}, new String[]{"11", "23"})).isFalse();

    }

}

