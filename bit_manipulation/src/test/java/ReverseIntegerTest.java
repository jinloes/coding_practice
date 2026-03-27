import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class ReverseIntegerTest {

    @Test
    void reverse() {
        assertThat(ReverseInteger.reverse(123)).isEqualTo(321);
        assertThat(ReverseInteger.reverse(-123)).isEqualTo(-321);
    }
}