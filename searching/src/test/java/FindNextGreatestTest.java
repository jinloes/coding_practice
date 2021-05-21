import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FindNextGreatestTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(List.of(-14, -10, 2, 108, 108, 243, 285, 285, 285, 401), 285, 9),
                Arguments.of(List.of(-14, -10, 2, 108, 108, 243, 285, 285, 285, 401), -13, 1)
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void search(List<Integer> toSearch, int val, int expected) {
        assertThat(FindNextGreatest.search(toSearch, val)).isEqualTo(expected);
    }
}