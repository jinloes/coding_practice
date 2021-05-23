import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PalindromicPermutationsTest {

    @Test
    void test1() {
        assertThat(PalindromicPermutations.test("edified")).isTrue();
    }
}