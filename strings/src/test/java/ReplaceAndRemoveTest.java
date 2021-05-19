import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReplaceAndRemoveTest {

    private static Stream<Arguments> strings() {
        return Stream.of(
                Arguments.of(new char[]{'a', 'b', 'a', 'c', ' '}, 4, 5),
                Arguments.of(new char[]{'a', 'c', 'a', 'a', ' ', ' ', ' '}, 4, 7)

        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void convert(char[] chars, int size, int expected) {
        assertThat(ReplaceAndRemove.replaceRemove(chars, size))
                .isEqualTo(expected);
    }
}