package com.jinloes.coding_practice.graphs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class PacificAtlanticWaterFlow {
  private Set<List<Integer>> atlanticPoints;
  private Set<List<Integer>> pacificPoints;

  public List<List<Integer>> pacificAtlantic(int[][] heights) {
    atlanticPoints = new HashSet<>();
    pacificPoints = new HashSet<>();

    int rows = heights.length;
    int cols = heights[0].length;

    for (int row = 0; row < rows; row++) {
      dfs(heights, row, 0, 0, true);

      dfs(heights, row, cols - 1, 0, false);
    }

    for (int col = 0; col < cols; col++) {
      dfs(heights, 0, col, 0, true);

      dfs(heights, rows - 1, col, 0, false);
    }

    List<List<Integer>> result = new ArrayList<>();

    for (List<Integer> point : atlanticPoints) {
      if (pacificPoints.contains(point)) {
        result.add(point);
      }
    }

    return result;
  }

  private void dfs(int[][] heights, int row, int col, int previousHeight, boolean pacificStart) {
    if (row < 0 || col < 0
        || row >= heights.length || col >= heights[0].length) {
      return;
    }

    if (previousHeight > heights[row][col]) {
      return;
    }

    List<Integer> point = List.of(row, col);
    if (pacificStart) {
      if (pacificPoints.contains(point)) {
        return;
      }
      pacificPoints.add(List.of(row, col));
    } else {
      if (atlanticPoints.contains(point)) {
        return;
      }
      atlanticPoints.add(List.of(row, col));
    }

    dfs(heights, row + 1, col, heights[row][col], pacificStart);
    dfs(heights, row - 1, col, heights[row][col], pacificStart);
    dfs(heights, row, col + 1, heights[row][col], pacificStart);
    dfs(heights, row, col - 1, heights[row][col], pacificStart);
  }
}

