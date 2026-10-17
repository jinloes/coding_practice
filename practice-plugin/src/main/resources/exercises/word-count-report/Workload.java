package com.jinloes.practice;

final class Workload { static final String UNIT = "token"; static final int[] SIZES = {2048,4096,8192,16384,32768}; private Workload() {} static Object prepare(int size) { StringBuilder text = new StringBuilder(size * 8); for (int i = 0; i < size; i++) text.append("word").append(i).append(' '); return text.toString(); } static void run(Object state) { if (Solution.countWords((String) state).isEmpty()) throw new IllegalStateException(); } static int units(int size) { return 1; } }
