import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FindNearestRepeatedEntriesTest {

    @Test
    void find() {
        assertThat(FindNearestRepeatedEntries.find(List.of("All", "work", "and", "no", "play", "makes", "for", "no", "work", "no", "fun", "and", "no", "results")))
                .isEqualTo(4);
    }
}