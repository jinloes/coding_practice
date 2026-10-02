package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {1 << 16, 1 << 17, 1 << 18, 1 << 19, 1 << 20, 1 << 21};

    private Workload() {
    }

    private static final int ROWS = 64;

    /** The size is the number of cells; the matrix grows in columns while the height stays fixed. */
    static Object prepare(int size) {
        int columns = size / ROWS;
        int[][] matrix = new int[ROWS][columns];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < columns; c++) {
                matrix[r][c] = r * columns + c;
            }
        }
        return matrix;
    }

    static void run(Object state) {
        int[][] matrix = (int[][]) state;
        int columns = matrix[0].length;
        int[] result = Solution.spiralOrder(matrix);
        if (result.length != ROWS * columns || result[0] != 0 || result[columns - 1] != columns - 1) {
            throw new IllegalStateException("Unexpected spiral");
        }
    }

    static int units(int size) {
        return 1;
    }
}
