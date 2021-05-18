import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KSmallestElementTest {
    @Test
    public void testCompute() {
        assertThat(KSmallestElement.compute(new int[]{1, 5, 2, 3, 7, 8, 100, 12, 10, 15}, 2)).isEqualTo(2);
    }
}
