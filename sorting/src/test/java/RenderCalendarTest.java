import io.vavr.Tuple2;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RenderCalendarTest {

    @Test
    void findMaxSimultaneousEvents() {
        List<Tuple2<Integer, Integer>> events = Lists.newArrayList(
                new Tuple2<>(1, 5),
                new Tuple2<>(2, 7),
                new Tuple2<>(4, 5),
                new Tuple2<>(6, 10),
                new Tuple2<>(8, 9),
                new Tuple2<>(9, 17),
                new Tuple2<>(11, 13),
                new Tuple2<>(12, 15),
                new Tuple2<>(14, 15)
        );

        assertThat(RenderCalendar.findMaxSimultaneousEvents(events)).isEqualTo(3);
    }
}