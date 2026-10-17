package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: grid and elimination count, for example: grid=[[0,0,0],[1,1,0],[0,0,0],[0,1,1],[0,0,0]]; k=1";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args)); return; }
        assertThat(Solution.shortestPath(new int[][]{{0,0,0},{1,1,0},{0,0,0},{0,1,1},{0,0,0}}, 1)).isEqualTo(6);
        assertThat(Solution.shortestPath(new int[][]{{0,1,1},{1,1,1},{1,0,0}}, 1)).isEqualTo(-1);
        System.out.println("Examples passed: 2");
    }
    private static void runInput(String input) {
        try {
            String[] parts = input.split(";\\s*k=");
            if (parts.length != 2 || !parts[0].startsWith("grid=")) throw new IllegalArgumentException();
            System.out.println(Solution.shortestPath(parseGrid(parts[0].substring(5)), Integer.parseInt(parts[1].trim())));
        } catch (RuntimeException exception) { System.out.println(USAGE); }
    }
    private static int[][] parseGrid(String text) {
        String body = text.trim();
        if (!body.startsWith("[[") || !body.endsWith("]]")) throw new IllegalArgumentException();
        String[] rows = body.substring(2, body.length() - 2).split("\\]\\s*,\\s*\\[");
        int[][] grid = new int[rows.length][];
        for (int r = 0; r < rows.length; r++) {
            String[] cells = rows[r].split("\\s*,\\s*");
            grid[r] = new int[cells.length];
            for (int c = 0; c < cells.length; c++) grid[r][c] = Integer.parseInt(cells[c]);
        }
        return grid;
    }
}
