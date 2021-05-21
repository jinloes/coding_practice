import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnIntervalTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(List.of(1, 2, 2, 4, 4, 4, 7, 11, 11, 13), 11, List.of(7, 8)),
                Arguments.of(List.of(11, 11, 11, 11, 11, 11, 11, 11, 11, 11), 11, List.of(0, 9))
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void get(List<Integer> toSearch, int val, List<Integer> expected) {
        assertThat(ReturnInterval.get(toSearch, val)).isEqualTo(expected);
    }
}