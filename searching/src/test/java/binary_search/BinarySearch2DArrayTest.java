package binary_search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BinarySearch2DArrayTest {
    private BinarySearch2DArray binarySearch2DArray;

    @BeforeEach
    void setUp() {
        binarySearch2DArray = new BinarySearch2DArray();
    }

    @ParameterizedTest
    @MethodSource("searchMatrixArgs")
    void searchMatrix(int[][] matrix, int target, boolean expected) {
        assertThat(binarySearch2DArray.searchMatrix(matrix, target))
                .isEqualTo(expected);
    }


    private static Stream<Arguments> searchMatrixArgs() {
        return Stream.of(
                Arguments.of(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 3, true),
                Arguments.of(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 60}}, 13, false),
                Arguments.of(new int[][]{{1, 3,}}, 3, true),
                Arguments.of(new int[][]{{1, 3, 5, 7}, {10, 11, 16, 20}, {23, 30, 34, 50}}, 5, true)
        );
    }
}