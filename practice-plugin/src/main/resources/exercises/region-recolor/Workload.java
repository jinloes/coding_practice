package com.jinloes.practice;

/** Scaling workload for Region Recolor; the image is rebuilt from a template for every repetition. */
final class Workload {
    static final String UNIT = "call";
    static final int ROWS = 8;
    static final int[] SIZES = {1024, 2048, 4096, 8192, 16384};

    private Workload() {}

    /** A comb-shaped region: full even rows joined by every fourth column, spanning about half of the pixels. */
    static Object prepare(int size) {
        int[][] template = new int[ROWS][size];
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < size; c++) {
                template[r][c] = r % 2 == 0 || c % 4 == 0 ? 1 : 0;
            }
        }
        return template;
    }

    static void run(Object state) {
        int[][] template = (int[][]) state;
        int[][] image = new int[template.length][];
        for (int r = 0; r < template.length; r++) {
            image[r] = template[r].clone();
        }
        int[][] result = Solution.recolor(image, 0, 0, 2);
        int last = template[0].length - 1;
        if (result[ROWS - 2][last] != 2 || result[1][1] != 0) {
            throw new IllegalStateException("Unexpected result");
        }
    }

    static int units(int size) {
        return 1;
    }
}
