import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class Search2DSortedListTest {

    public static Stream<Arguments> lists() {
        List<List<Integer>> data = List.of(
                List.of(-1, 2, 4, 4, 6),
                List.of(1, 5, 5, 9, 21),
                List.of(3, 6, 6, 9, 22),
                List.of(3, 6, 8, 10, 24),
                List.of(6, 8, 9, 12, 25),
                List.of(8, 10, 12, 13, 40)
        );

        return Stream.of(
                Arguments.of(data, 7, false),
                Arguments.of(data, 8, true)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void search(List<List<Integer>> data, int val, boolean expected) {
        assertThat(Search2DSortedList.search(data, val)).isEqualTo(expected);
    }
}