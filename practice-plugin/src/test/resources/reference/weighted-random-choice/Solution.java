package com.jinloes.practice;
import java.util.Random;
public class Solution { public static int choose(int[] values,double[] weights,Random random){ double total=0.0; for(double w:weights) total+=w; double threshold=random.nextDouble()*total; double seen=0.0; for(int i=0;i<values.length;i++){ seen+=weights[i]; if(threshold<seen) return values[i]; } return values[values.length-1]; } }
