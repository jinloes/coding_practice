package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {128, 256, 512, 1024, 2048};

    private Workload() {
    }

    /** The matrix is rebuilt for every repetition because rotateClockwise mutates it; size is the side length n. */
    static Object prepare(int size) {
        int[][] matrix = new int[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                matrix[r][c] = r * size + c;
            }
        }
        return matrix;
    }

    static void run(Object state) {
        int[][] matrix = (int[][]) state;
        int n = matrix.length;
        Solution.rotateClockwise(matrix);
        if (matrix[0][n - 1] != 0 || matrix[0][0] != (n - 1) * n) {
            throw new IllegalStateException("Matrix was not rotated clockwise");
        }
    }

    static int units(int size) {
        return 1;
    }
}
