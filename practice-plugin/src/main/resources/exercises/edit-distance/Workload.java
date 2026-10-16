package com.jinloes.practice;

/** Scaling workload for Edit Distance; both words have length equal to the size. */
final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {}

    static Object prepare(int size) {
        StringBuilder from = new StringBuilder();
        StringBuilder to = new StringBuilder();
        for (int i = 0; i < size; i++) {
            from.append((char) ('a' + (i * 7) % 26));
            to.append((char) ('a' + (i * 11) % 26));
        }
        return new String[]{from.toString(), to.toString()};
    }

    static void run(Object state) {
        String[] words = (String[]) state;
        int edits = Solution.minEdits(words[0], words[1]);
        if (edits < 0 || edits > words[0].length()) {
            throw new IllegalStateException("Edit count out of range: " + edits);
        }
    }

    static int units(int size) {
        return 1;
    }
}
