import com.google.common.collect.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SortNearlySortedListTest {

    public static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList(3, -1, 2, 6, 4, 5, 8), 2,
                        List.of(-1, 2, 3, 4, 5, 6, 8))
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void sort(List<Integer> toSort, int k, List<Integer> expected) {
        assertThat(SortNearlySortedList.sort(toSort, k)).isEqualTo(expected);
    }
}