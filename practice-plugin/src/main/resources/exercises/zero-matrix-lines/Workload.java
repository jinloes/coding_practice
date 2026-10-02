package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 16, 1 << 17, 1 << 18, 1 << 19, 1 << 20, 1 << 21};

    private Workload() {
    }

    private static final int COLUMNS = 64;

    /** The matrix is rebuilt for every repetition because zeroLines mutates it; size is the cell count. */
    static Object prepare(int size) {
        int rows = size / COLUMNS;
        int[][] matrix = new int[rows][COLUMNS];
        for (int[] row : matrix) {
            java.util.Arrays.fill(row, 1);
        }
        matrix[rows / 2][COLUMNS / 2] = 0;
        return matrix;
    }

    static void run(Object state) {
        int[][] matrix = (int[][]) state;
        Solution.zeroLines(matrix);
        int rows = matrix.length;
        if (matrix[rows / 2][0] != 0 || matrix[0][COLUMNS / 2] != 0 || matrix[0][0] != 1) {
            throw new IllegalStateException("Zero lines were not cleared correctly");
        }
    }

    static int units(int size) {
        return 1;
    }
}
