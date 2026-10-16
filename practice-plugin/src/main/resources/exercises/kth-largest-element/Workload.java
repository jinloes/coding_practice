package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 * The rank is the median, so the work grows with the input size.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};

    private Workload() {}

    static Object prepare(int size) {
        int[] nums = new int[size];
        long state = 13;
        for (int i = 0; i < size; i++) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            nums[i] = (int) ((state >>> 33) % 1_000_000);
        }
        return nums;
    }

    static void run(Object state) {
        int[] nums = (int[]) state;
        int median = Solution.findKthLargest(nums, nums.length / 2);
        if (median < 0 || median >= 1_000_000) {
            throw new IllegalStateException("Value out of range: " + median);
        }
    }

    static int units(int size) {
        return 1;
    }
}
