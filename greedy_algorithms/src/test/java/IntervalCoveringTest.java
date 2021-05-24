import io.vavr.Tuple;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IntervalCoveringTest {

    @Test
    void findMinimumVisits() {
        assertThat(IntervalCovering.findMinimumVisits(Lists.newArrayList(
                Tuple.of(1, 2),
                Tuple.of(2, 3),
                Tuple.of(3, 4),
                Tuple.of(2, 3),
                Tuple.of(3, 4),
                Tuple.of(4, 5)
        ))).containsExactly(2, 4);
    }
}