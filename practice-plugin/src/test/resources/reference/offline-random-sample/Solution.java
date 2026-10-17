package com.jinloes.practice;
import java.util.Random;
public class Solution { public static void samplePrefix(int[] values,int k,Random random){ for(int i=0;i<k;i++){ int chosen=i+random.nextInt(values.length-i); int tmp=values[i]; values[i]=values[chosen]; values[chosen]=tmp; } } }
