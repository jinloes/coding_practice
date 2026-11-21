package com.jinloes.practice;

final class Workload {
    static final String UNIT = "routing";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] patterns = new String[size];
        String[] handlers = new String[size];
        String[] queries = new String[size];
        for (int i = 0; i < size; i++) {
            patterns[i] = "/svc-" + (i / 4) + "/" + ((i & 1) == 0 ? "*" : "item-" + i) + "/view";
            handlers[i] = "h" + i;
            queries[i] = "/svc-" + (i / 4) + "/item-" + i + "/view";
        }
        return new Object[]{patterns, handlers, queries};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        String[] patterns = (String[]) data[0];
        String[] handlers = (String[]) data[1];
        String[] queries = (String[]) data[2];
        Solution router = new Solution();
        for (int i = 0; i < patterns.length; i++) {
            router.register(patterns[i], handlers[i]);
        }
        int matched = 0;
        for (String query : queries) {
            if (!router.match(query).isEmpty()) {
                matched++;
            }
        }
        if (matched != queries.length) {
            throw new IllegalStateException("Unexpected match count: " + matched);
        }
    }

    static int units(int size) {
        return 1;
    }
}
