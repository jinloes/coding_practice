import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class PaintingTest {

    @ParameterizedTest
    @MethodSource("housesAndExpectedProvider")
    public void maxProfit(int[] houses, int expected) {
        assertThat(Painting.maxProfit(houses)).isEqualTo(expected);
    }

    static Stream<Arguments> housesAndExpectedProvider() {
        return Stream.of(
                Arguments.arguments(new int[]{}, 0),
                Arguments.arguments(new int[]{100}, 100),
                Arguments.arguments(new int[]{100, 50}, 150),
                Arguments.arguments(new int[]{100, 70, 60}, 170),
                Arguments.arguments(new int[]{60, 70, 100}, 170),
                Arguments.arguments(new int[]{40, 100, 50, 20, 100}, 260)
        );
    }
}