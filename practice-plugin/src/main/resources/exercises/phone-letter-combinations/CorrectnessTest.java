package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CorrectnessTest {
    @Test
    void singleDigitWithThreeLetters() {
        assertThat(Solution.combinations("2"))
                .containsExactlyInAnyOrder("a", "b", "c");
    }

    @Test
    void singleDigitNine() {
        assertThat(Solution.combinations("9"))
                .containsExactlyInAnyOrder("w", "x", "y", "z");
    }

    @Test
    void repeatedDigits() {
        assertThat(Solution.combinations("22"))
                .containsExactlyInAnyOrder("aa", "ab", "ac", "ba", "bb", "bc", "ca", "cb", "cc");
    }

    @Test
    void digitsWithFourLetters() {
        assertThat(Solution.combinations("79"))
                .containsExactlyInAnyOrder("pw", "px", "py", "pz", "qw", "qx", "qy", "qz", "rw", "rx", "ry", "rz", "sw", "sx", "sy", "sz");
    }

    @Test
    void threeDigits() {
        assertThat(Solution.combinations("567"))
                .containsExactlyInAnyOrder("jmp", "jmq", "jmr", "jms", "jnp", "jnq", "jnr", "jns", "jop", "joq", "jor", "jos", "kmp", "kmq", "kmr", "kms", "knp", "knq", "knr", "kns", "kop", "koq", "kor", "kos", "lmp", "lmq", "lmr", "lms", "lnp", "lnq", "lnr", "lns", "lop", "loq", "lor", "los");
    }

    @Test
    void rejectsOne() {
        assertThatThrownBy(() -> Solution.combinations("21"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsZero() {
        assertThatThrownBy(() -> Solution.combinations("0"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsNonDigits() {
        assertThatThrownBy(() -> Solution.combinations("2a"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void producesTheExpectedCountWithoutDuplicates() {
        List<String> result = Solution.combinations("2379");
        assertThat(result).hasSize(3 * 3 * 4 * 4).doesNotHaveDuplicates()
                .allSatisfy(text -> assertThat(text).hasSize(4));
    }

    @Test
    void keepsLettersInDigitOrder() {
        assertThat(Solution.combinations("92")).containsExactlyInAnyOrder("wa", "wb", "wc", "xa", "xb", "xc",
                "ya", "yb", "yc", "za", "zb", "zc");
    }
}
