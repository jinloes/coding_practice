package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] keys = new String[size];
        String[] vals = new String[size];
        for (int i = 0; i < size; i++) {
            keys[i] = "k" + i;
            vals[i] = "v" + i;
        }
        return new String[][]{keys, vals};
    }

    static void run(Object state) {
        String[][] data = (String[][]) state;
        String[] keys = data[0];
        String[] vals = data[1];
        Solution store = new Solution();
        int n = keys.length;
        for (int i = 0; i < n; i++) {
            store.set(keys[i], vals[i]);
        }
        store.begin();
        for (int i = 0; i < n; i += 2) {
            store.delete(keys[i]);
        }
        int found = 0;
        for (int i = 0; i < n; i++) {
            if (store.get(keys[i]) != null) {
                found++;
            }
        }
        store.commit();
        if (found != n / 2) {
            throw new IllegalStateException("Unexpected live key count: " + found);
        }
    }

    static int units(int size) {
        return 1;
    }
}
