package com.jinloes.practice;

import java.util.Arrays;
import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: matrix rows separated by semicolons, then the target, for example: [1, 4, 7]; [2, 5, 8]; [3, 6, 9] | 5";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.contains(new int[][]{{1,4,7},{2,5,8},{3,6,9}}, 5)).isTrue();
        assertThat(Solution.contains(new int[][]{{1,4,7},{2,5,8},{3,6,9}}, 10)).isFalse();
        assertThat(Solution.contains(new int[][]{{-5,-1},{-3,4}}, -3)).isTrue();
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] parts=input.split("\\|"); if(parts.length!=2){System.out.println(USAGE);return;} int[][] m=parseMatrix(parts[0]); int target=Integer.parseInt(parts[1].trim()); System.out.println("contains("+Arrays.deepToString(m)+", "+target+") = "+Solution.contains(m,target)); }
        catch (RuntimeException exception) { System.out.println("Could not read matrix search input. " + USAGE); }
    }
    private static int[][] parseMatrix(String text) { String[] rows=text.trim().split(";"); int[][] m=new int[rows.length][]; for(int r=0;r<rows.length;r++) m[r]=parseInts(rows[r]); return m; }
    private static int[] parseInts(String input) { String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] t=cleaned.split("\s+"); int[] v=new int[t.length]; for(int i=0;i<t.length;i++) v[i]=Integer.parseInt(t[i]); return v; }
}

