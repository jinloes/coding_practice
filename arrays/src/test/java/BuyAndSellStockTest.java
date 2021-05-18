import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class BuyAndSellStockTest {

    private static Stream<Arguments> arraysForMaxProfit() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of(new int[]{310, 315, 275, 295, 260, 270, 290, 230, 255, 250}, 30)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForMaxProfit")
    void maxProfit(int[] arr, int maxProfit) {
        assertThat(BuyAndSellStock.maxProfit(arr)).isEqualTo(maxProfit);
    }
}