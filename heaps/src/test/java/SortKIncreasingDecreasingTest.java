import com.google.common.collect.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SortKIncreasingDecreasingTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList(57, 131, 493, 294, 221, 339, 418, 452, 442, 190),
                        List.of(57, 131, 190, 221, 294, 339, 418, 442, 452, 493)),
                Arguments.of(Lists.newArrayList(1, 2, 3, 4, 5, 4, 3, 2, 1),
                        List.of(1, 1, 2, 2, 3, 3, 4, 4, 5))
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void sort(List<Integer> toSort, List<Integer> expected) {
        assertThat(SortKIncreasingDecreasing.sort(toSort)).isEqualTo(expected);
    }
}