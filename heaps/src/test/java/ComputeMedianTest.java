import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ComputeMedianTest {

    @Test
    void onlineMedian() {
        assertThat(ComputeMedian.onlineMedian(List.of(1, 0, 3, 5, 2, 0, 1).iterator()))
                .containsExactly(1.0, 0.5, 1.0, 2.0, 2.0, 1.5, 1.0);
    }
}