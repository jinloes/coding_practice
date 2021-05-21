import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SearchCyclicallySortedArrayTest {

    @Test
    void search() {
        assertThat(SearchCyclicallySortedArray.search(List.of(378, 478, 550, 631, 103, 203, 220, 234, 279, 368)))
                .isEqualTo(4);
    }
}