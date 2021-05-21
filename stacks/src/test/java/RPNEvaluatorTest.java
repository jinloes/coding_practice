import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class RPNEvaluatorTest {

    private static Stream<Arguments> expressions() {

        return Stream.of(
                Arguments.of("1", 1),
                Arguments.of("3,4,+,2,x,1,+", 15)
        );
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void eval(String expression, int result) {
        assertThat(RPNEvaluator.eval(expression)).isEqualTo(result);
    }
}