import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AdvanceThroughArrayTest {

    private static Stream<Arguments> arraysForAdvance() {
        return Stream.of(Arguments.of(null, true),
                Arguments.of(new int[]{3, 3, 1, 0, 2, 0, 1}, true),
                Arguments.of(new int[]{2, 4, 1, 1, 0, 2, 3}, true),
                Arguments.of(new int[]{3, 2, 0, 0, 2, 0, 1}, false)

        );
    }

    @ParameterizedTest
    @MethodSource("arraysForAdvance")
    void canAdvance(int[] arr, boolean result) {
        assertThat(AdvanceThroughArray.canAdvance(arr)).isEqualTo(result);
    }
}