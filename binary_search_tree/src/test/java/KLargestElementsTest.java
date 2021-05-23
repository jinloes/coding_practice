import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KLargestElementsTest extends BaseBSTTest {

    @Test
    void get() {
        assertThat(KLargestElements.get(root, 3)).containsExactly(53, 47, 43);
    }
}