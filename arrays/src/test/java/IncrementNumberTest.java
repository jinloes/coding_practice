import org.assertj.core.util.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class IncrementNumberTest {

    public static Stream<Arguments> numbersForIncrement() {

        return Stream.of(
                Arguments.of(Lists.newArrayList(1, 2, 3, 4, 5), Lists.newArrayList(1, 2, 3, 4, 6)),
                Arguments.of(Lists.newArrayList(1, 2, 3, 4, 9), Lists.newArrayList(1, 2, 3, 5, 0)),
                Arguments.of(Lists.newArrayList(1, 9, 9, 9, 9), Lists.newArrayList(2, 0, 0, 0, 0)),
                Arguments.of(Lists.newArrayList(9, 9, 9, 9, 9), Lists.newArrayList(1, 0, 0, 0, 0, 0))
        );
    }

    @ParameterizedTest
    @MethodSource("numbersForIncrement")
    public void increment(List<Integer> input, List<Integer> expected) {

        IncrementNumber.increment(input);

        assertThat(input).isEqualTo(expected);
    }
}