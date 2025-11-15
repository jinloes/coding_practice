package binary_search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SearchRangeTest {
    private SearchRange searchRange;

    @BeforeEach
    void setUp() {
        searchRange = new SearchRange();
    }

    @Test
    void searchRange() {
        assertThat(searchRange.searchRange(new int[]{5, 7, 7, 8, 8, 10}, 8))
                .containsExactly(3, 4);
    }
}