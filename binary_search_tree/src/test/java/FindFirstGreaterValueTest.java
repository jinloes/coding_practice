import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FindFirstGreaterValueTest extends BaseBSTTest {

    @Test
    void findFirstGreaterThanK() {
        assertThat(FindFirstGreaterValue.findFirstGreaterThanK(root, 23).data).isEqualTo(29);
    }
}