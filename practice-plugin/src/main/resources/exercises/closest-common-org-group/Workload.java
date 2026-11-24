package com.jinloes.practice;

final class Workload {
    static final String UNIT = "run";
    static final int[] SIZES = {4_096, 8_192, 16_384, 32_768, 65_536};

    private Workload() {
    }

    static Object prepare(int size) {
        String[][] links = new String[size - 1][2];
        for (int i = 1; i < size; i++) {
            links[i - 1][0] = "G" + ((i - 1) / 2);
            links[i - 1][1] = "G" + i;
        }
        String[][] memberships = new String[size][2];
        for (int i = 0; i < size; i++) {
            memberships[i][0] = "G" + i;
            memberships[i][1] = "E" + i;
        }
        String[] targets = {"E" + (size - 1), "E" + (size - 2), "E" + (size / 2)};
        return new Object[]{links, memberships, targets};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        String group = new Solution().closestCommonGroup(
                (String[][]) data[0], (String[][]) data[1], (String[]) data[2]);
        if (group == null) {
            throw new IllegalStateException("Expected non-null group");
        }
    }

    static int units(int size) {
        return 1;
    }
}
