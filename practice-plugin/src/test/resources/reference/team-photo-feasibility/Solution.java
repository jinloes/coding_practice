package com.jinloes.practice;
import java.util.Arrays;
public class Solution { public static boolean canTakePhoto(int[] red,int[] blue){ int[] r=red.clone(), b=blue.clone(); Arrays.sort(r); Arrays.sort(b); return before(r,b)||before(b,r); } static boolean before(int[] front,int[] back){ for(int i=0;i<front.length;i++) if(front[i]>=back[i]) return false; return true; } }
