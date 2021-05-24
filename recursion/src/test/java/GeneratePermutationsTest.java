import com.google.common.collect.Lists;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratePermutationsTest {

    private static Stream<Arguments> lists() {
        return Stream.of(
                Arguments.of(Lists.newArrayList(2, 3, 5, 7),
                        List.of(List.of(2, 3, 5, 7),
                                List.of(2, 3, 7, 5),
                                List.of(2, 5, 3, 7),
                                List.of(2, 5, 7, 3),
                                List.of(2, 7, 3, 5),
                                List.of(2, 7, 5, 3),
                                List.of(3, 2, 5, 7),
                                List.of(3, 2, 7, 5),
                                List.of(3, 5, 2, 7),
                                List.of(3, 5, 7, 2),
                                List.of(3, 7, 2, 5),
                                List.of(3, 7, 5, 2),
                                List.of(5, 2, 3, 7),
                                List.of(5, 2, 7, 3),
                                List.of(5, 3, 2, 7),
                                List.of(5, 3, 7, 2),
                                List.of(5, 7, 2, 3),
                                List.of(5, 7, 3, 2),
                                List.of(7, 2, 3, 5),
                                List.of(7, 2, 5, 3),
                                List.of(7, 3, 2, 5),
                                List.of(7, 3, 5, 2),
                                List.of(7, 5, 2, 3),
                                List.of(7, 5, 3, 2)
                        ),
                        Arguments.of(Lists.newArrayList(2, 2, 3, 0),
                                List.of(
                                        List.of(2, 2, 0, 3),
                                        List.of(2, 2, 3, 0),
                                        List.of(2, 0, 2, 3),
                                        List.of(2, 0, 3, 2),
                                        List.of(2, 3, 2, 0),
                                        List.of(2, 3, 0, 2),
                                        List.of(0, 2, 2, 3),
                                        List.of(0, 2, 3, 2),
                                        List.of(0, 3, 2, 2),
                                        List.of(3, 2, 2, 0),
                                        List.of(3, 2, 0, 2),
                                        List.of(3, 0, 2, 2)
                                )
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void permutations(List<Integer> candidates, List<List<Integer>> solutions) {
        assertThat(GeneratePermutations.permutations(candidates)).containsExactlyInAnyOrderElementsOf(solutions);
    }
}