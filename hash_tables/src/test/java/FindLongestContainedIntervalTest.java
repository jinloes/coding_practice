import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindLongestContainedIntervalTest {

    @Test
    void getLength() {
        assertThat(FindLongestContainedInterval.getLength(List.of(10, 5, 3, 11, 6, 100, 4))).isEqualTo(4);
    }
}