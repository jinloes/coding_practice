import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FindSmallestSubarrayTest {

    @Test
    void shortestSubarray() {
        assertThat(FindSmallestSubarray.shortestSubarray(
                List.of("apple", "banana", "apple", "apple", "dog", "cat", "apple", "dog", "banana", "apple", "cat", "dog"),
                Set.of("banana", "cat"))).containsExactly(8, 10);
    }
}