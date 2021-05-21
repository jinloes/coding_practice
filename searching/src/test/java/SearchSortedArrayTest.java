import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SearchSortedArrayTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10), 4, 3),
                Arguments.of(List.of(1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10), 40, -1),
                Arguments.of(List.of(1, 2, 3, 4, 5, 6, 6, 7, 8, 9, 10), 6, 5)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void search(List<Integer> toSearch, int val, int expected) {
        assertThat(SearchSortedArray.search(toSearch, val)).isEqualTo(expected);
    }
}