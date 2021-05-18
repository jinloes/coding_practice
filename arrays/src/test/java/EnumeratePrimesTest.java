import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EnumeratePrimesTest {

    private static Stream<Arguments> primeCandidates() {
        return Stream.of(
                Arguments.of(1, new ArrayList<>()),
                Arguments.of(1, new ArrayList<>()),
                Arguments.of(2, List.of(2)),
                Arguments.of(18, List.of(2, 3, 5, 7, 11, 13, 17))
        );
    }

    @ParameterizedTest
    @MethodSource("primeCandidates")
    void enumerate(int upperBound, List<Integer> expected) {
        assertThat(EnumeratePrimes.enumerate(upperBound)).isEqualTo(expected);
    }
}