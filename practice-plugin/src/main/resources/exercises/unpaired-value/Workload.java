package com.jinloes.practice;

/**
 * Scaling workload. Input construction happens in prepare, outside the timed region.
 */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {32768, 65536, 131072, 262144, 524288};
    private static final int LONE = 987654;

    private Workload() {}

    static Object prepare(int size) {
        int[] nums = new int[size + 1 - size % 2];
        long state = 29;
        for (int i = 0; i + 1 < nums.length; i += 2) {
            state = state * 6364136223846793005L + 1442695040888963407L;
            int value = (int) (state >>> 33);
            nums[i] = value;
            nums[i + 1] = value;
        }
        nums[nums.length - 1] = LONE;
        return nums;
    }

    static void run(Object state) {
        int[] nums = (int[]) state;
        int found = 0;
        for (int repeat = 0; repeat < 8; repeat++) {
            found = Solution.findUnpaired(nums);
        }
        if (found != LONE) {
            throw new IllegalStateException("Expected " + LONE + " but got " + found);
        }
    }

    static int units(int size) {
        return 1;
    }
}
