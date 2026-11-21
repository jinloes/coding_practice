package com.jinloes.practice;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    private final int width;
    private final int height;
    private final int[][] food;
    private final Deque<Long> body = new ArrayDeque<>();
    private final Set<Long> occupied = new HashSet<>();
    private int foodIndex;
    private int score;
    private boolean gameOver;

    public Solution(int width, int height, int[][] food) {
        if (width < 1 || height < 1 || food == null) {
            throw new IllegalArgumentException("Invalid board setup.");
        }
        this.width = width;
        this.height = height;
        this.food = new int[food.length][2];
        for (int i = 0; i < food.length; i++) {
            if (food[i] == null || food[i].length != 2) {
                throw new IllegalArgumentException("Invalid food entry.");
            }
            this.food[i][0] = food[i][0];
            this.food[i][1] = food[i][1];
        }
        long start = pack(0, 0);
        body.addFirst(start);
        occupied.add(start);
    }

    public int step(char direction) {
        int deltaRow = 0;
        int deltaCol = 0;
        switch (direction) {
            case 'U' -> deltaRow = -1;
            case 'D' -> deltaRow = 1;
            case 'L' -> deltaCol = -1;
            case 'R' -> deltaCol = 1;
            default -> throw new IllegalArgumentException("Unsupported direction: " + direction);
        }
        if (gameOver) {
            return -1;
        }
        long head = body.peekFirst();
        int nextRow = unpackRow(head) + deltaRow;
        int nextCol = unpackCol(head) + deltaCol;
        if (nextRow < 0 || nextRow >= height || nextCol < 0 || nextCol >= width) {
            gameOver = true;
            return -1;
        }
        boolean eating = foodIndex < food.length
                && food[foodIndex][0] == nextRow
                && food[foodIndex][1] == nextCol;
        if (!eating) {
            long tail = body.removeLast();
            occupied.remove(tail);
        }
        long next = pack(nextRow, nextCol);
        if (!occupied.add(next)) {
            gameOver = true;
            return -1;
        }
        body.addFirst(next);
        if (eating) {
            foodIndex++;
            score++;
        }
        return score;
    }

    private static long pack(int row, int col) {
        return (((long) row) << 32) | (col & 0xffffffffL);
    }

    private static int unpackRow(long packed) {
        return (int) (packed >> 32);
    }

    private static int unpackCol(long packed) {
        return (int) packed;
    }
}
