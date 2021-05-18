import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PalindromicNumberTest {

    @Test
    void check() {
        assertThat(PalindromicNumber.check(12321)).isTrue();
        assertThat(PalindromicNumber.check(-12321)).isFalse();
        assertThat(PalindromicNumber.check(12341)).isFalse();
    }
}