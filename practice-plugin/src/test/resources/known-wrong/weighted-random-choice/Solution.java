package com.jinloes.practice;
import java.util.Random;
public class Solution { public static int choose(int[] values, double[] weights, Random random) { return values[random.nextInt(values.length)]; } }
