import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class DeleteValueTest {

    private static Stream<Arguments> arraysForDelete() {
        return Stream.of(
                Arguments.of(null, 4, 0),
                Arguments.of(new int[]{1}, 1, 0),
                Arguments.of(new int[]{2, 3, 5, 5, 7, 11, 11, 11, 13}, 11, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForDelete")
    void delete(int[] arr, int valueToDelete, int size) {
        assertThat(DeleteValue.delete(arr, valueToDelete)).isEqualTo(size);
    }
}