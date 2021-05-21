import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FindKthLargestElementTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(List.of(3, 2, 1, 5, 4), 1, 5),
                Arguments.of(List.of(3, 2, 1, 5, 4), 3, 3),
                Arguments.of(List.of(3, 2, 1, 5, 4), 5, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void search(List<Integer> toSearch, int k, int result) {
        assertThat(FindKthLargestElement.find(toSearch, k)).isEqualTo(result);
    }
}