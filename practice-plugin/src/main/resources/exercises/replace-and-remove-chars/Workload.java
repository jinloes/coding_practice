package com.jinloes.practice;

final class Workload { static final String UNIT = "character"; static final int[] SIZES = {4096,8192,16384,32768,65536}; private Workload() {} static Object prepare(int size) { char[] buffer = new char[size * 2 + 4]; for (int i = 0; i < size; i++) buffer[i] = "abcz".charAt(i % 4); return new Object[]{buffer, size}; } static void run(Object state) { Object[] data = (Object[]) state; int length = Solution.replaceAndRemove((char[]) data[0], (Integer) data[1]); if (length <= 0) throw new IllegalStateException(); } static int units(int size) { return 1; } }
