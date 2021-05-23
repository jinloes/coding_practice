import com.google.common.collect.Lists;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MergingIntervalsTest {

    @Test
    void merge() {
        List<Tuple2<Integer, Integer>> intervals = Lists.newArrayList(
                Tuple.of(-4, -1),
                Tuple.of(0, 2),
                Tuple.of(3, 6),
                Tuple.of(7, 9),
                Tuple.of(11, 12),
                Tuple.of(14, 17),
                Tuple.of(1, 8)
        );

        assertThat(MergingIntervals.merge(intervals))
                .containsExactly(
                        Tuple.of(-4, -1),
                        Tuple.of(0, 9),
                        Tuple.of(11, 12),
                        Tuple.of(14, 17)
                );
    }
}