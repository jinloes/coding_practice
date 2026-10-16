package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void emptyStringHasOneEmptyPartition() {
        assertThat(Solution.partitions("")).map(List::toString)
                .containsExactlyInAnyOrder("[]");
    }

    @Test
    void singleCharacter() {
        assertThat(Solution.partitions("x")).map(List::toString)
                .containsExactlyInAnyOrder("[x]");
    }

    @Test
    void equalLettersMergeInEveryWay() {
        assertThat(Solution.partitions("aaa")).map(List::toString)
                .containsExactlyInAnyOrder("[a, a, a]", "[a, aa]", "[aa, a]", "[aaa]");
    }

    @Test
    void rejectsPiecesWhoseEndsMatchButInsideDoesNot() {
        assertThat(Solution.partitions("abca")).map(List::toString)
                .containsExactlyInAnyOrder("[a, b, c, a]");
    }

    @Test
    void isCaseSensitive() {
        assertThat(Solution.partitions("aA")).map(List::toString)
                .containsExactlyInAnyOrder("[a, A]");
    }

    @Test
    void evenLengthPalindrome() {
        assertThat(Solution.partitions("abba")).map(List::toString)
                .containsExactlyInAnyOrder("[a, b, b, a]", "[a, bb, a]", "[abba]");
    }

    @Test
    void noMergesPossible() {
        assertThat(Solution.partitions("abcd")).map(List::toString)
                .containsExactlyInAnyOrder("[a, b, c, d]");
    }

    @Test
    void overlappingPalindromes() {
        assertThat(Solution.partitions("abac")).map(List::toString)
                .containsExactlyInAnyOrder("[a, b, a, c]", "[aba, c]");
    }

    @Test
    void handlesALongStringWithoutPalindromesQuickly() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            text.append("abc");
        }
        assertThat(Solution.partitions(text.toString())).hasSize(1);
        assertThat(Solution.partitions(text.toString()).get(0)).hasSize(30);
    }

    @Test
    void countsPartitionsOfARun() {
        assertThat(Solution.partitions("aaaaaaaaaa")).hasSize(512).doesNotHaveDuplicates();
    }

    @Test
    void everyPieceIsAPalindromeAndJoinsBackToTheText() {
        String text = "abacabadaba";
        for (List<String> partition : Solution.partitions(text)) {
            assertThat(String.join("", partition)).isEqualTo(text);
            assertThat(partition).allSatisfy(piece ->
                    assertThat(new StringBuilder(piece).reverse().toString()).isEqualTo(piece));
        }
    }
}
