

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ArrayOccurrenceTest {

    @Test
    void emptyArray() {
        assertThat(ArrayOccurrence.findOccurrences(new int[]{}, 5)).isEqualTo(0);
    }

    @Test
    void nullArray() {
        assertThat(ArrayOccurrence.findOccurrences(null, 5)).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("occurrenceCases")
    void findOccurrences(int[] arr, int num, int expected) {
        assertThat(ArrayOccurrence.findOccurrences(arr, num)).isEqualTo(expected);
    }

    static Stream<Object[]> occurrenceCases() {
        return Stream.of(
            new Object[]{new int[]{5}, 5, 1},
            new Object[]{new int[]{1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13}, 6, 3},
            new Object[]{new int[]{1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13}, 1, 2},
            new Object[]{new int[]{1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13}, 13, 2},
            new Object[]{new int[]{1, 1, 4, 4, 4, 4, 6, 6, 6, 8, 8, 8, 10, 10, 12, 13, 13}, 4, 4},
            new Object[]{new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 1, 15},
            new Object[]{new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}, 2, 0}
        );
    }
}