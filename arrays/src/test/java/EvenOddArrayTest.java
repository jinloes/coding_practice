import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class EvenOddArrayTest {

    private static Stream<Arguments> arraysForPartition() {
        Random rd = new Random();
        int size = rd.nextInt(1000);
        int[] original = new int[size];

        for (int i = 0; i < original.length; i++) {
            original[i] = rd.nextInt();
        }

        int[] copy = Arrays.copyOf(original, size);


        return Stream.of(Arguments.of(null, null),
                Arguments.of(new int[]{1, 2, 3, 4, 5}, new int[]{1, 2, 3, 4, 5}),
                Arguments.of(new int[]{1}, new int[]{1}),
                Arguments.of(new int[]{2}, new int[]{2}),
                Arguments.of(original, copy));
    }

    @ParameterizedTest
    @MethodSource("arraysForPartition")
    void partition(int[] original, int[] arr) {
        EvenOddArray.partition(arr);

        if (original == null) {
            assertThat(arr).isNull();
            return;
        }

        assertThat(arr)
                .containsOnly(original)
                .satisfies((result) -> {
                    boolean odd = false;
                    for (int i = 0; i < result.length; i++) {
                        if (Math.abs(arr[i] % 2) == 1) {
                            odd = true;
                        }
                        if (!odd) {
                            assertThat(arr[i]).isEven();
                        } else {
                            assertThat(arr[i]).isOdd();
                        }
                    }
                });
    }
}