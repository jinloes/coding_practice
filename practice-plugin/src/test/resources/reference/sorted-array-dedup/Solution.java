package com.jinloes.practice;
public class Solution { public static int deduplicate(int[] numbers) { if (numbers.length == 0) return 0; int write = 1; for (int read = 1; read < numbers.length; read++) if (numbers[read] != numbers[write - 1]) numbers[write++] = numbers[read]; return write; } }
