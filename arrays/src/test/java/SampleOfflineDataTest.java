import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SampleOfflineDataTest {

    @Test
    void sample() {
        List<Integer> original = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8);
        List<Integer> copy = new ArrayList<>(original);

        SampleOfflineData.sample(copy, 5);

        assertThat(copy).containsExactlyInAnyOrderElementsOf(original);
    }
}