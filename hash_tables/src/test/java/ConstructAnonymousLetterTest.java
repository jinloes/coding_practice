import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructAnonymousLetterTest {

    public static Stream<Arguments> input() {

        return Stream.of(
                Arguments.of("aa", "ab", false),
                Arguments.of("aa", "aab", true)
        );
    }

    @ParameterizedTest
    @MethodSource("input")
    void isLetterConstructibleFromMagazine(String note, String magazine, boolean expected) {
        assertThat(ConstructAnonymousLetter.isLetterConstructibleFromMagazine(note, magazine)).isEqualTo(expected);
    }
}