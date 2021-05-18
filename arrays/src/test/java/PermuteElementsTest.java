import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PermuteElementsTest {

    private static Stream<Arguments> permuteArrays() {
        return Stream.of(
                Arguments.of(new int[]{2, 3, 1, 4}, new int[]{1, 2, 3, 4}, new int[]{2, 0, 1, 3})
        );
    }

    @ParameterizedTest
    @MethodSource("permuteArrays")
    void permute(int[] expected, int[] elements, int[] permutations) {
        PermuteElements.permute(elements, permutations);

        assertThat(elements).isEqualTo(expected);
    }
}