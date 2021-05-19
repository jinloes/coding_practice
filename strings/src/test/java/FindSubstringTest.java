import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class FindSubstringTest {

    private static Stream<Arguments> strings() {
        return Stream.of(
                Arguments.of("hi bye test", "foo", -1),
                Arguments.of("aabaacaab", "aacaa", 3),
                Arguments.of("GACGCCA", "CGC", 2)

        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void convert(String toSearch, String toFind, int expected) {
        assertThat(FindSubstring.find(toSearch, toFind)).isEqualTo(expected);
    }
}