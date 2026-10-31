package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void decodesSideBySideGroups() {
        check("aaabcbc", "3[a]2[bc]");
    }

    @Test
    void decodesANestedGroup() {
        check("xyyyxyyyz", "2[x3[y]]z");
    }

    @Test
    void readsMultiDigitCounts() {
        check("kkkkkkkkkk", "10[k]");
    }

    private static void check(String expected, String encoded) {
        assertThat(Solution.decode(encoded)).as("decode(\"%s\")", encoded).isEqualTo(expected);
    }
}
