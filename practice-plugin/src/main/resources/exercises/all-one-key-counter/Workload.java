package com.jinloes.practice;

final class Workload {
    static final String UNIT = "operation";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        String[] keys = new String[size];
        for (int i = 0; i < size; i++) keys[i] = "k" + i;
        return keys;
    }

    static void run(Object state) {
        String[] keys = (String[]) state;
        Solution counter = new Solution();
        for (String key : keys) {
            counter.inc(key);
            counter.inc(key);
        }
        for (int i = 0; i < keys.length; i += 2) {
            counter.dec(keys[i]);
        }
        if (counter.getMaxKey().isEmpty() || counter.getMinKey().isEmpty()) {
            throw new IllegalStateException("Counter lost all keys");
        }
    }

    static int units(int size) {
        return 1;
    }
}

