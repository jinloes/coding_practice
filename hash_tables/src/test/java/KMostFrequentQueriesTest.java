import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KMostFrequentQueriesTest {

    @Test
    void compute() {
        List<String> queries = List.of("a", "a", "b", "c", "c", "c", "d", "a");

        assertThat(KMostFrequentQueries.compute(queries, 2)).containsExactly("c", "a");
    }
}