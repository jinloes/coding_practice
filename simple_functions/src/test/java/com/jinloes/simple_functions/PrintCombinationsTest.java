package com.jinloes.simple_functions;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PrintCombinationsTest {
    @Test
    public void testPrintCombinations() {
        assertThat(PrintCombinations.compute("1?")).containsExactly("10", "11");
        assertThat(PrintCombinations.compute("1?0?")).containsExactly("1000", "1001", "1100", "1101");
    }
}
