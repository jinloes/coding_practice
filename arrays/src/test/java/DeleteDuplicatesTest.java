import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteDuplicatesTest {

    private static Stream<Arguments> arraysForRemove() {
        return Stream.of(
                Arguments.of(null, 0),
                Arguments.of(new int[]{1}, 1),
                Arguments.of(new int[]{2, 3, 5, 5, 7, 11, 11, 11, 13}, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForRemove")
    void remove(int[] arr, int validElements) {
        assertThat(DeleteDuplicates.remove(arr)).isEqualTo(validElements);
    }
}