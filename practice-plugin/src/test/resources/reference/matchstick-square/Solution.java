package com.jinloes.practice;

import java.util.Arrays;

public class Solution {
    public static boolean canFormSquare(int[] matchsticks) {
        if (matchsticks.length < 4) return false;
        int total=0; for (int s: matchsticks) total += s;
        if (total == 0 || total % 4 != 0) return false;
        int side = total / 4; int[] sticks = matchsticks.clone(); Arrays.sort(sticks);
        for (int l=0,r=sticks.length-1;l<r;l++,r--) { int t=sticks[l]; sticks[l]=sticks[r]; sticks[r]=t; }
        return place(sticks,0,new int[4],side);
    }
    private static boolean place(int[] sticks, int index, int[] sides, int side) {
        if (index == sticks.length) return sides[0]==side && sides[1]==side && sides[2]==side && sides[3]==side;
        int stick = sticks[index];
        for (int i=0;i<4;i++) {
            if (sides[i] + stick <= side) { sides[i]+=stick; if (place(sticks,index+1,sides,side)) return true; sides[i]-=stick; }
            if (sides[i] == 0) break;
        }
        return false;
    }
}
