import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LongestSubarrayWithDistinctEntriesTest {

    @Test
    void get() {
        assertThat(LongestSubarrayWithDistinctEntries.get(List.of('f', 's', 'f', 'e', 't', 'w', 'e', 'n', 'w', 'e')))
                .isEqualTo(5);
    }
}