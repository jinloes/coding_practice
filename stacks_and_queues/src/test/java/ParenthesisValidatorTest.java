import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ParenthesisValidatorTest {

    private static Stream<Arguments> expressions() {

        return Stream.of(
                Arguments.of("()(())", true),
                Arguments.of("([]){()}", true),
                Arguments.of("[()[]|()()|]", true),
                Arguments.of("{)", false),
                Arguments.of("[()[]{()()", false)
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void validate(String expression, boolean expected) {
        assertThat(ParenthesisValidator.validate(expression)).isEqualTo(expected);
    }
}