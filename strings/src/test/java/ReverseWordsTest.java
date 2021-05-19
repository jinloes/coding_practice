import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ReverseWordsTest {

    private static Stream<Arguments> strings() {
        return Stream.of(
                Arguments.of("ram is costly", "costly is ram")
        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void convert(String text, String expected) {
        assertThat(ReverseWords.reverse(text)).isEqualTo(expected);
    }
}