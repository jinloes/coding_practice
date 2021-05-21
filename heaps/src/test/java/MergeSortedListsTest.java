import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class MergeSortedListsTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(List.of(List.of(1, 2, 3, 4, 5), List.of(3, 6, 10, 11, 12)),
                        List.of(1, 2, 3, 3, 4, 5, 6, 10, 11, 12)),
                Arguments.of(List.of(List.of(3, 5, 7), List.of(0, 6), List.of(0, 6, 28)),
                        List.of(0, 0, 3, 5, 6, 6, 7, 28))
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void merge(List<List<Integer>> lists, List<Integer> expected) {
        assertThat(MergeSortedLists.merge(lists)).isEqualTo(expected);
    }
}