import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IntervalCoveringTest {

    @Test
    public void findMinimumVisits() {
        assertThat(IntervalCovering.findMinimumVisits(
                new ArrayList<>(List.of(
                        new IntervalCovering.Interval(1, 2),
                        new IntervalCovering.Interval(2, 3),
                        new IntervalCovering.Interval(3, 4),
                        new IntervalCovering.Interval(2, 3),
                        new IntervalCovering.Interval(3, 4),
                        new IntervalCovering.Interval(4, 5)
                )))
        ).containsExactly(2, 4);
    }
}