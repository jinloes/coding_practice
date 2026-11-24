package com.jinloes.practice;

public final class Solution {
    private final int[] current;
    private int currentSnap;

    public Solution(int length) {
        this.current = new int[length];
    }

    public void set(int index, int val) {
        current[index] = val;
    }

    public int snap() {
        return currentSnap++;
    }

    public int get(int index, int snapId) {
        // Bug: ignores snapId and returns live value.
        return current[index];
    }
}
