package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: values then a semicolon then permutation, for example: [10, 20, 30]; [2, 0, 1]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertRun(new int[]{10,20,30},new int[]{2,0,1},new int[]{20,30,10}); assertRun(new int[]{4,5,6},new int[]{0,1,2},new int[]{4,5,6}); assertRun(new int[]{7,8,9,10},new int[]{1,0,3,2},new int[]{8,7,10,9}); System.out.println("Examples passed: 3"); } private static void assertRun(int[] v,int[] p,int[] e){ Solution.applyPermutation(v,p); assertThat(v).containsExactly(e); } private static void runInput(String input){ try{ String[] parts=input.split(";",-1); if(parts.length!=2) throw new IllegalArgumentException(); int[] values=parseInts(parts[0]); int[] permutation=parseInts(parts[1]); Solution.applyPermutation(values,permutation); System.out.println(Arrays.toString(values)); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] tokens=cleaned.split("\\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; } }

