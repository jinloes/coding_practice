package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    @Test
    void singleCellReachesBothOceans() {
        assertThat(cells(new int[][]{{5}})).containsExactlyInAnyOrder("0,0");
    }

    @Test
    void singleRowTouchesBothOceans() {
        assertThat(cells(new int[][]{{1, 2, 3}})).containsExactlyInAnyOrder("0,0", "0,1", "0,2");
    }

    @Test
    void equalHeightsAllowFlowAcrossPlateaus() {
        assertThat(cells(new int[][]{{1, 2, 2}, {3, 2, 3}, {2, 4, 5}}))
                .containsExactlyInAnyOrder("0,1", "0,2", "1,0", "1,1", "1,2", "2,0", "2,1", "2,2");
    }

    private static List<String> cells(int[][] heights) {
        List<String> result = new ArrayList<>();
        for (int[] cell : Solution.drainageCells(heights)) {
            result.add(cell[0] + "," + cell[1]);
        }
        return result;
    }
}
