package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final String ORDER = "qwertyuiopasdfghjklzxcvbnm";
    static final int[] SIZES = {2048, 4096, 8192, 16384, 32768};

    private Workload() {}

    /** Word i spells i in base 26, four digits long, with digit d written as ORDER.charAt(d), so the list is sorted. */
    static Object prepare(int size) {
        String[] words = new String[size];
        for (int i = 0; i < size; i++) {
            char[] letters = new char[4];
            int rest = i;
            for (int d = 3; d >= 0; d--) {
                letters[d] = ORDER.charAt(rest % 26);
                rest /= 26;
            }
            words[i] = new String(letters);
        }
        return words;
    }

    static void run(Object state) {
        String order = Solution.alienOrder((String[]) state);
        if (!ORDER.equals(order)) {
            throw new IllegalStateException("Expected " + ORDER + " but got " + order);
        }
    }

    static int units(int size) { return 1; }
}
