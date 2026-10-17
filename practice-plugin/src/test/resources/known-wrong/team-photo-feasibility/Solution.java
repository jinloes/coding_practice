package com.jinloes.practice;
import java.util.Arrays;
public class Solution { public static boolean canTakePhoto(int[] red,int[] blue){ int[] r=red.clone(), b=blue.clone(); Arrays.sort(r); Arrays.sort(b); for(int i=0;i<r.length;i++) if(r[i]>b[i]) return false; return true; } }
