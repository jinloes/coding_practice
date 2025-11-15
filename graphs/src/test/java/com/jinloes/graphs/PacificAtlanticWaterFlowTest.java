package com.jinloes.coding_practice.graphs;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.MethodSource;

class PacificAtlanticWaterFlowTest {
  private PacificAtlanticWaterFlow waterFlow;

  @BeforeEach
  void setUp() {
    waterFlow = new PacificAtlanticWaterFlow();
  }

  @ParameterizedTest
  @MethodSource("pacificAtlanticArgs")
  void pacificAtlantic(int[][] heights, List<List<Integer>> expected) {
    assertThat(waterFlow.pacificAtlantic(heights))
        .containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> pacificAtlanticArgs() {
    return Stream.of(
        Arguments.of(new int[][] {
                new int[] {4, 2, 7, 3, 4},
                new int[] {7, 4, 6, 4, 7},
                new int[] {6, 3, 5, 3, 6}
            }, List.of(List.of(0, 2),
                List.of(0, 4),
                List.of(1, 0),
                List.of(1, 1),
                List.of(1, 2),
                List.of(1, 3),
                List.of(1, 4),
                List.of(2, 0)
            )
        ),
        Arguments.of(
            new int[][] {
                new int[] {1},
                new int[] {1}
            }, List.of(
                List.of(0, 0),
                List.of(1, 0)
            )
        )
    );
  }
}