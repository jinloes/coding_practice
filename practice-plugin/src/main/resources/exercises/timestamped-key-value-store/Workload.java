package com.jinloes.practice;

/**
 * Scaling workload for Timestamped Key-Value Store. n is the number of set calls (spread over 8 keys,
 * with increasing times) followed by n get calls at scattered times.
 */
final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4096, 8192, 16384, 32768, 65536};
    private static final int KEYS = 8;

    private Workload() {}

    static Object prepare(int size) {
        String[] keys = new String[KEYS];
        for (int k = 0; k < KEYS; k++) {
            keys[k] = "key" + k;
        }
        String[] values = new String[size];
        for (int i = 0; i < size; i++) {
            values[i] = "v" + i;
        }
        return new Object[] {keys, values};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        String[] keys = (String[]) data[0];
        String[] values = (String[]) data[1];
        int n = values.length;
        Solution store = new Solution();
        for (int i = 0; i < n; i++) {
            store.set(keys[i % KEYS], values[i], i + 1);
        }
        long hits = 0;
        for (int i = 0; i < n; i++) {
            int time = (int) ((i * 7919L) % (n + 2));
            if (!store.get(keys[i % KEYS], time).isEmpty()) {
                hits++;
            }
        }
        if (hits == 0 || !store.get(keys[0], Integer.MAX_VALUE).equals(values[n - KEYS])
                || !store.get("missing", 5).isEmpty()) {
            throw new IllegalStateException("Unexpected store contents");
        }
    }

    static int units(int size) {
        return 1;
    }
}
