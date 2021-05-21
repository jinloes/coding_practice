import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class SunsetViewTest {

    private static Stream<Arguments> buildings() {

        return Stream.of(
                Arguments.of(List.of(1, 3, 5, 4, 2, 1), List.of(2, 3, 4, 5)),
                Arguments.of(List.of(10, 3, 5, 4, 2, 6), List.of(0, 5))
        );
    }

    @ParameterizedTest
    @MethodSource("buildings")
    void canView(List<Integer> buildings, List<Integer> expected) {
        assertThat(SunsetView.canView(buildings)).isEqualTo(expected);
    }
}