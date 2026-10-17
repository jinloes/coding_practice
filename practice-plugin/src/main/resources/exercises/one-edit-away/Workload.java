package com.jinloes.practice;

final class Workload { static final String UNIT = "character"; static final int[] SIZES = {4096,8192,16384,32768,65536}; private Workload() {} static Object prepare(int size) { String base = "a".repeat(size); return new String[]{base, base.substring(0, size - 1) + "b"}; } static void run(Object state) { String[] data = (String[]) state; if (!Solution.isOneEditAway(data[0], data[1])) throw new IllegalStateException(); } static int units(int size) { return 1; } }
