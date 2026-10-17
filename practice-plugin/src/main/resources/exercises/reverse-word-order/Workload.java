package com.jinloes.practice;

final class Workload { static final String UNIT = "character"; static final int[] SIZES = {4096,8192,16384,32768,65536}; private Workload() {} static Object prepare(int size) { StringBuilder b = new StringBuilder(size + 32); for (int i = 0; b.length() < size; i++) b.append("word").append(i).append(' '); return b.toString(); } static void run(Object state) { if (Solution.reverseWords((String) state).isEmpty()) throw new IllegalStateException(); } static int units(int size) { return 1; } }
