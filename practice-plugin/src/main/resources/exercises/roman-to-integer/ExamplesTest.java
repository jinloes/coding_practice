package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void addsRepeatedSymbols() {
        assertThat(Solution.romanToInt("III")).as("romanToInt(\"III\")").isEqualTo(3);
    }

    @Test
    void addsSymbolsFromLargestToSmallest() {
        assertThat(Solution.romanToInt("LVIII")).as("romanToInt(\"LVIII\")").isEqualTo(58);
    }

    @Test
    void subtractsEachSmallerSymbolBeforeALargerOne() {
        assertThat(Solution.romanToInt("MCMXCIV"))
                .as("romanToInt(\"MCMXCIV\") must apply the CM, XC, and IV subtractive pairs")
                .isEqualTo(1994);
    }
}
