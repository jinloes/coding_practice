import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateMatchedParenStringsTest {

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of(2, List.of("(())", "()()")),
                Arguments.of(3, List.of("((()))", "(()())", "(())()", "()(())", "()()()"))
        );
    }

    @ParameterizedTest
    @MethodSource("params")
    void generate(int numParens, List<String> expected) {
        assertThat(GenerateMatchedParenStrings.generate(numParens))
                .containsExactlyInAnyOrderElementsOf(expected);
    }
}