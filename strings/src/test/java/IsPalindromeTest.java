import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class IsPalindromeTest {

    private static Stream<Arguments> strings() {
        return Stream.of(
                Arguments.of("Able was I, ere I saw Elba!", true),
                Arguments.of("A man, a plan, a canal, Panama.", true),
                Arguments.of("Ray a Ray", false)

        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void convert(String text, boolean expected) {
        assertThat(IsPalindrome.check(text)).isEqualTo(expected);
    }
}