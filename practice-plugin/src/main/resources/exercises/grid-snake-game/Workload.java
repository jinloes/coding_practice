package com.jinloes.practice;

final class Workload {
    static final String UNIT = "simulation";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        int[][] food = new int[size][2];
        char[] moves = new char[size * 2];
        int row = 0;
        int col = 0;
        int width = 64;
        int moveIndex = 0;
        int foodIndex = 0;
        while (moveIndex < moves.length) {
            boolean goingRight = (row & 1) == 0;
            if ((goingRight && col + 1 < width) || (!goingRight && col - 1 >= 0)) {
                moves[moveIndex] = goingRight ? 'R' : 'L';
                col += goingRight ? 1 : -1;
            } else {
                moves[moveIndex] = 'D';
                row++;
            }
            if ((moveIndex & 1) == 0 && foodIndex < food.length) {
                food[foodIndex][0] = row;
                food[foodIndex][1] = col;
                foodIndex++;
            }
            moveIndex++;
        }
        return new Object[]{width, row + 2, food, moves};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        int width = (Integer) data[0];
        int height = (Integer) data[1];
        int[][] food = (int[][]) data[2];
        char[] moves = (char[]) data[3];
        Solution game = new Solution(width, height, food);
        int last = 0;
        for (char move : moves) {
            last = game.step(move);
        }
        if (last < 0) {
            throw new IllegalStateException("Unexpected collision in valid snake walk");
        }
    }

    static int units(int size) {
        return 1;
    }
}
