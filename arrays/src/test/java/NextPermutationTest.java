import org.assertj.core.util.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class NextPermutationTest {

    private static Stream<Arguments> permuteArrays() {
        return Stream.of(
                Arguments.of(Lists.newArrayList(1, 0, 3, 2), 1203),
                Arguments.of(Lists.newArrayList(6, 2, 1, 5, 4, 3, 0), 6230145)
        );
    }

    private static Stream<Arguments> permuteSmallerArrays() {
        return Stream.of(
                Arguments.of(Lists.newArrayList(1, 0), 1),
                Arguments.of(Lists.newArrayList(3, 1, 0, 5), 3051)
        );
    }

    @ParameterizedTest
    @MethodSource("permuteArrays")
    void compute(List<Integer> digits, int expected) {
        assertThat(NextPermutation.compute(digits)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("permuteArrays")
    void computeBruteForce(List<Integer> digits, int expected) {
        assertThat(NextPermutation.computeBruteForce(digits)).isEqualTo(expected);
    }

    @ParameterizedTest
    @MethodSource("permuteSmallerArrays")
    void nextSmaller(List<Integer> digits, int expected) {
        assertThat(NextPermutation.nextSmaller(digits)).isEqualTo(expected);
    }
}