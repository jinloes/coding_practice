package com.jinloes.practice;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: values then a semicolon then weights, for example: [10, 20, 30]; [1.0, 3.0, 6.0]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.choose(new int[]{10,20,30},new double[]{1,3,6},new Random(1))).isIn(10,20,30); assertThat(Solution.choose(new int[]{7},new double[]{5},new Random(1))).isEqualTo(7); assertThat(Solution.choose(new int[]{4,9},new double[]{1,1},new Random(2))).isIn(4,9); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ String[] parts=input.split(";",-1); if(parts.length!=2) throw new IllegalArgumentException(); System.out.println(Solution.choose(parseInts(parts[0]),parseDoubles(parts[1]),new Random(1))); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); String[] tokens=cleaned.split("\\s+"); int[] v=new int[tokens.length]; for(int i=0;i<tokens.length;i++) v[i]=Integer.parseInt(tokens[i]); return v; } private static double[] parseDoubles(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); String[] tokens=cleaned.split("\\s+"); double[] v=new double[tokens.length]; for(int i=0;i<tokens.length;i++) v[i]=Double.parseDouble(tokens[i]); return v; } }

