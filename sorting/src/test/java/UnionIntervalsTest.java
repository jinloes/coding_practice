import com.google.common.collect.Lists;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class UnionIntervalsTest {

    @Test
    void unionOfIntervals() {
        List<Tuple2<Integer, Integer>> intervals = Lists.newArrayList(
                Tuple.of(0, 3),
                Tuple.of(1, 1),
                Tuple.of(2, 4),
                Tuple.of(3, 4),
                Tuple.of(5, 7),
                Tuple.of(7, 8),
                Tuple.of(8, 11),
                Tuple.of(9, 11),
                Tuple.of(12, 14),
                Tuple.of(13, 15),
                Tuple.of(12, 16),
                Tuple.of(16, 17)
        );

        assertThat(UnionIntervals.unionOfIntervals(intervals))
                .containsExactly(Tuple.of(0, 4), Tuple.of(5, 11), Tuple.of(12, 17));
    }
}