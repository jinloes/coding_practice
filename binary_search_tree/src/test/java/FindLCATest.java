import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FindLCATest extends BaseBSTTest {

    @Test
    void compute() {
        assertThat(FindLCA.compute(root, l2a, l3d)).isEqualTo(l1a);
        assertThat(FindLCA.compute(root, l2a, l1a)).isEqualTo(l1a);
    }
}