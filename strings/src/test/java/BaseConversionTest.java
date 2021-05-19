import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BaseConversionTest {

    private static Stream<Arguments> numbersToConvert() {
        return Stream.of(
                Arguments.of("615", 7, 13, "1A7"),
                Arguments.of("-1234", 6, 16, "-136")

        );
    }

    @ParameterizedTest
    @MethodSource("numbersToConvert")
    void convert(String number, int base1, int base2, String expected) {
        assertThat(BaseConversion.convert(number, base1, base2))
                .isEqualToIgnoringCase(expected);
    }
}