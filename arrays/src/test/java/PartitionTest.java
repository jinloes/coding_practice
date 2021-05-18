import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class PartitionTest {

    private static Stream<Arguments> arraysForPartition() {
        int[] arr4 = new int[]{3, 2, 100, 3, 1, -4, -6, -6, 3, 5};

        return Stream.of(Arguments.of(null, null, 0),
                Arguments.of(new int[]{3, 2, 3, 1, 4, 5}, new int[]{3, 2, 3, 1, 4, 5}, 2),
                Arguments.of(new int[]{1}, new int[]{1}, 0),
                Arguments.of(new int[]{2}, new int[]{2}, 0),
                Arguments.of(arr4, Arrays.copyOf(arr4, arr4.length), 2)
        );
    }

    @ParameterizedTest
    @MethodSource("arraysForPartition")
    void partition(int[] original, int[] arr, int pivotIdx) {
        Partition.partition(arr, pivotIdx);

        if (original == null) {
            assertThat(arr).isNull();
            return;
        }

        System.out.println(Arrays.toString(arr));
        int pivotVal = original[pivotIdx];

        assertThat(arr).containsOnly(original);

        assertThat(arr)
                .satisfies(result -> {
                    boolean lower = true;
                    boolean higher = false;

                    for (int i = 0; i < result.length; i++) {
                        if (result[i] == pivotVal && !higher) {
                            lower = false;
                        } else if (result[i] > pivotVal && !lower) {
                            higher = true;
                        }

                        if (lower) {
                            assertThat(result[i]).isLessThan(pivotVal);
                        } else if (higher) {
                            assertThat(result[i]).isGreaterThan(pivotVal);
                        } else {
                            assertThat(result[i]).isEqualTo(pivotVal);
                        }
                    }
                });
    }
}