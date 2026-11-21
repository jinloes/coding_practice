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

    public Solution(int width, int height, int[][] food) {
        this.width = width;
        this.height = height;
        this.food = food;
        body.addFirst(0L);
        occupied.add(0L);
    }

    public int step(char direction) {
        long head = body.peekFirst();
        int row = (int) (head >> 32);
        int col = (int) head;
        switch (direction) {
            case 'U' -> row--;
            case 'D' -> row++;
            case 'L' -> col--;
            case 'R' -> col++;
            default -> throw new IllegalArgumentException();
        }
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return -1;
        }
        long next = (((long) row) << 32) | (col & 0xffffffffL);
        // Bug: checks self-collision before vacating the tail on non-eating moves.
        if (occupied.contains(next)) {
            return -1;
        }
        if (foodIndex < food.length && food[foodIndex][0] == row && food[foodIndex][1] == col) {
            foodIndex++;
            score++;
        } else {
            occupied.remove(body.removeLast());
        }
        body.addFirst(next);
        occupied.add(next);
        return score;
    }
}
