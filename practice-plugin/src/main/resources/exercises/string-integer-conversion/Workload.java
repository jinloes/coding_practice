package com.jinloes.practice;

final class Workload { static final String UNIT = "conversion"; static final int[] SIZES = {4096,8192,16384,32768,65536}; private Workload() {} static Object prepare(int size) { return size; } static void run(Object state) { int size = (Integer) state; int checksum = 0; for (int i = 0; i < size; i++) { int value = i * 7919; String text = Solution.intToString(value); checksum += Solution.stringToInt(text); } if (checksum == Integer.MIN_VALUE) throw new IllegalStateException(); } static int units(int size) { return size; } }
