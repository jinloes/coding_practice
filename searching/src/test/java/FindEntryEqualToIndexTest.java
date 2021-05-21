import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindEntryEqualToIndexTest {
    @Test
    void find() {
        assertThat(FindEntryEqualToIndex.find(List.of(-2, 0, 2, 3, 6, 7, 9)))
                .isIn(2, 3);
    }
}