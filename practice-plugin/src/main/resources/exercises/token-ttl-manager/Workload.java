package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] ids = new String[size];
        for (int i = 0; i < size; i++) {
            ids[i] = "tok_" + i;
        }
        return ids;
    }

    static void run(Object state) {
        String[] ids = (String[]) state;
        Solution manager = new Solution(500);
        int t = 1;
        long total = 0;
        for (int i = 0; i < ids.length; i++) {
            manager.generate(ids[i], t++);
            if (i >= 10 && (i & 3) == 0) {
                manager.renew(ids[i - 10], t++);
            }
            total += manager.countUnexpired(t++);
        }
        if (total < 0) {
            throw new IllegalStateException("Unreachable");
        }
    }

    static int units(int size) {
        return 1;
    }
}
