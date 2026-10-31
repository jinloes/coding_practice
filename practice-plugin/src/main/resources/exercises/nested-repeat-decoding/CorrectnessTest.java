package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyAndPlainTextDecodeToThemselves() {
        check("", "");
        check("abc", "abc");
        check("z", "z");
    }

    @Test
    void countOfOneKeepsTheText() {
        check("ab", "1[ab]");
        check("q", "1[1[1[q]]]");
    }

    @Test
    void keepsTextAroundGroups() {
        check("abccd", "ab2[c]d");
        check("xyyzwww", "x2[y]z3[w]");
        check("aab", "2[a]b");
    }

    @Test
    void readsMultiDigitCounts() {
        check("a".repeat(12), "12[a]");
        check("z".repeat(300), "300[z]");
        check("aaaaaaaaaab".repeat(2), "2[10[a]b]");
        check("xy".repeat(105), "105[xy]");
    }

    @Test
    void decodesSeveralNestingLevels() {
        check("abccbccabccbccabccbcc", "3[a2[b2[c]]]");
        check("abbccabbcc", "2[a2[b]2[c]]");
        check("mnnpmnnp", "2[m2[n]p]");
    }

    @Test
    void emptyGroupsDecodeToNothing() {
        check("ab", "a2[]b");
        check("", "3[2[]]");
        check("cc", "2[1[]c]");
    }

    @Test
    void handlesDeepNesting() {
        String encoded = "1[".repeat(1_000) + "x" + "]".repeat(1_000);
        check("x", encoded);
    }

    @Test
    void producesLongOutput() {
        check("abcdefghij".repeat(10_000), "100[100[abcdefghij]]");
        check("ab".repeat(50_000), "250[200[ab]]");
    }

    private static void check(String expected, String encoded) {
        String shown = encoded.length() > 60 ? encoded.substring(0, 60) + "..." : encoded;
        assertThat(Solution.decode(encoded)).as("decode(\"%s\")", shown).isEqualTo(expected);
    }
}
