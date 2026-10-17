package com.jinloes.practice;



import org.junit.jupiter.api.Test;



import static org.assertj.core.api.Assertions.assertThat;



class ExamplesTest {

    @Test

    void findsPatternAcrossRowsAtSameColumn() {

        assertThat(Solution.containsPattern(new String[]{"12345", "56789", "11123"}, new String[]{"78", "12"}))

                .isTrue();

    }



    @Test

    void rejectsRowsThatDoNotAlign() {

        assertThat(Solution.containsPattern(new String[]{"12345", "56789", "11123"}, new String[]{"89", "12"}))

                .isFalse();

    }

}

