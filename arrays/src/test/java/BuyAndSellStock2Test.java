import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BuyAndSellStock2Test {

    private static Stream<Arguments> arraysForMaxProfit() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of(new int[]{12, 11, 13, 9, 12, 8, 14, 13, 15}, 10)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForMaxProfit")
    void maxProfit(int[] prices, int maxProfit) {
        assertThat(BuyAndSellStock2.maxProfit(prices)).isEqualTo(maxProfit);
    }
}