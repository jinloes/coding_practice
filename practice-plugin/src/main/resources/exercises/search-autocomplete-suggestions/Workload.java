package com.jinloes.practice;

final class Workload {
    static final String UNIT = "call";
    static final int[] SIZES = {2_000, 4_000, 8_000, 16_000, 32_000};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] products = new String[size];
        for (int i = 0; i < size; i++) {
            int mixed = (int) (((i * 2654435761L) & 0x7fffffffL) % size);
            char c0 = (char) ('a' + (mixed % 26));
            char c1 = (char) ('a' + ((mixed / 26) % 26));
            char c2 = (char) ('a' + ((mixed / 676) % 26));
            products[i] = "" + c0 + c1 + c2 + String.format("%05d", i);
        }
        return products;
    }

    static void run(Object state) {
        String[] products = (String[]) state;
        var suggestions = Solution.suggest(products, "aaa");
        if (suggestions.size() != 3 || suggestions.get(0).isEmpty()) {
            throw new IllegalStateException("Expected non-empty autocomplete results");
        }
    }

    static int units(int size) {
        return 1;
    }
}
