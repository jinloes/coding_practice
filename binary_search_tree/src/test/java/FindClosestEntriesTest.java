import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FindClosestEntriesTest {

    @Test
    void compute() {
        assertThat(FindClosestEntries.compute(Lists.newArrayList(
                Lists.newArrayList(5, 10, 15),
                Lists.newArrayList(3, 6, 9, 12, 15),
                Lists.newArrayList(8, 16, 24)
        ))).containsExactly(15, 15, 16);
    }
}