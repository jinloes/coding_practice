package com.jinloes.practice;

import java.util.Arrays;

public final class Solution {
    private static final int INITIAL_CAPACITY = 4;

    private final int[][] snapIds;
    private final int[][] values;
    private final int[] counts;
    private int currentSnap;

    public Solution(int length) {
        this.snapIds = new int[length][];
        this.values = new int[length][];
        this.counts = new int[length];
        this.currentSnap = 0;
    }

    public void set(int index, int val) {
        int count = counts[index];
        if (count > 0 && snapIds[index][count - 1] == currentSnap) {
            values[index][count - 1] = val;
            return;
        }
        if (snapIds[index] == null) {
            snapIds[index] = new int[INITIAL_CAPACITY];
            values[index] = new int[INITIAL_CAPACITY];
        } else if (count == snapIds[index].length) {
            int nextCap = count * 2;
            snapIds[index] = Arrays.copyOf(snapIds[index], nextCap);
            values[index] = Arrays.copyOf(values[index], nextCap);
        }
        snapIds[index][count] = currentSnap;
        values[index][count] = val;
        counts[index] = count + 1;
    }

    public int snap() {
        return currentSnap++;
    }

    public int get(int index, int snapId) {
        int count = counts[index];
        if (count == 0) {
            return 0;
        }
        int[] ids = snapIds[index];
        int low = 0;
        int high = count - 1;
        int found = -1;
        while (low <= high) {
            int mid = low + ((high - low) >>> 1);
            if (ids[mid] <= snapId) {
                found = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return found >= 0 ? values[index][found] : 0;
    }
}
