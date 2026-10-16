package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int REPEATS = 8;

    private Workload() {}

    static Object prepare(int size) {
        int[] nums = new int[size + 1];
        for (int i = 0; i < size; i++) {
            nums[i] = i + 1;
        }
        nums[size] = size / 2;
        return nums;
    }

    static void run(Object state) {
        int[] nums = (int[]) state;
        int expected = (nums.length - 1) / 2;
        for (int i = 0; i < REPEATS; i++) {
            if (Solution.findDuplicate(nums) != expected) {
                throw new IllegalStateException("Wrong duplicate");
            }
        }
    }

    static int units(int size) { return 1; }
}
