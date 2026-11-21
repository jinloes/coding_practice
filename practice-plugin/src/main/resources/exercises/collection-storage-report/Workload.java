package com.jinloes.practice;

import java.util.List;

final class Workload {
    static final String UNIT = "report";
    static final int[] SIZES = {256, 512, 1024, 2048, 4096};

    private Workload() {
    }

    static Object prepare(int size) {
        String[] files = new String[size];
        String[] primary = new String[size];
        String[] secondary = new String[size];
        for (int i = 0; i < size; i++) {
            files[i] = "file-" + i;
            primary[i] = "col-" + (i % (size / 2));
            secondary[i] = "col-" + ((i * 7 + 3) % (size / 2));
        }
        return new Object[]{files, primary, secondary};
    }

    static void run(Object state) {
        Object[] data = (Object[]) state;
        String[] files = (String[]) data[0];
        String[] primary = (String[]) data[1];
        String[] secondary = (String[]) data[2];
        Solution report = new Solution();
        for (int i = 0; i < files.length; i++) {
            report.putFile(files[i], (i % 97) + 10L, List.of(primary[i], secondary[i]));
        }
        List<String> top = report.topCollections(10);
        if (report.totalBytes() <= 0 || top.isEmpty()) {
            throw new IllegalStateException("Unexpected empty report");
        }
    }

    static int units(int size) {
        return 1;
    }
}
