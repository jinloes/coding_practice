import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class StringIntConversionTest {

    private static Stream<Arguments> strings() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of("", 0),
                Arguments.of("1234", 1234),
                Arguments.of("-1234", -1234)

        );
    }

    private static Stream<Arguments> numbers() {
        return Stream.of(
                Arguments.of(0, "0"),
                Arguments.of(1234, "1234"),
                Arguments.of(-100, "-100")
        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void fromString(String str, int number) {
        assertThat(StringIntConversion.fromString(str)).isEqualTo(number);
    }

    @ParameterizedTest
    @MethodSource("numbers")
    void fromInt(int number, String str) {
        assertThat(StringIntConversion.fromInt(number)).isEqualTo(str);
    }
}