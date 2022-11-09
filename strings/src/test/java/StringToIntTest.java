import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringToIntTest {

    @Test
    public void convertToInt() {
        assertThat(StringToInt.convertToInt("-123")).isEqualTo(-123);
    }

    @Test
    public void intToString() {
        assertThat(StringToInt.intToString(-123)).isEqualTo("-123");
    }
}