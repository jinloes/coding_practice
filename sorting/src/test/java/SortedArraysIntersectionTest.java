import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SortedArraysIntersectionTest {

    @Test
    void getIntersection() {
        assertThat(SortedArraysIntersection.getIntersection(List.of(2, 3, 3, 5, 5, 6, 7, 7, 8, 12),
                List.of(5, 5, 6, 8, 8, 9, 10, 10)))
                .containsExactly(5, 6, 8);
    }
}