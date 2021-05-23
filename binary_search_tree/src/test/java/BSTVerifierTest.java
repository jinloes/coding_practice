import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BSTVerifierTest {

    private static Stream<Arguments> trees() {
        BSTNode<Integer> invalid = new BSTNode<>(10, new BSTNode<>(5, new BSTNode<>(1, new BSTNode<>(6, null, null), null), null),
                null);
        BSTNode<Integer> valid = new BSTNode<>(10, new BSTNode<>(5, new BSTNode<>(1, null, null), new BSTNode<>(6, null, null)),
                null);

        return Stream.of(
                Arguments.of(invalid, false),
                Arguments.of(valid, true)

        );
    }

    @ParameterizedTest
    @MethodSource("trees")
    void isValid(BSTNode<Integer> root, boolean expected) {
        assertThat(BSTVerifier.isValid(root)).isEqualTo(expected);
    }
}