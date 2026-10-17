package com.jinloes.practice;

import java.util.Arrays; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: array values followed by sample size, for example: [0, 1, 2, 3] 2"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} int[] a={0,1,2,3}; Solution.samplePrefix(a,2,new Random(1)); assertThat(Arrays.stream(Arrays.copyOf(a,2)).distinct().count()).isEqualTo(2); int[] b={5,6,7}; Solution.samplePrefix(b,0,new Random(1)); assertThat(Arrays.copyOf(b,0)).isEmpty(); int[] c={8,9}; Solution.samplePrefix(c,2,new Random(1)); assertThat(c).containsExactlyInAnyOrder(8,9); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ int[] parsed=parseInts(input); if(parsed.length==0) throw new IllegalArgumentException(); int k=parsed[parsed.length-1]; int[] values=Arrays.copyOf(parsed,parsed.length-1); Solution.samplePrefix(values,k,new Random(1)); System.out.println(Arrays.toString(Arrays.copyOf(values,k))); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); String[] tokens=cleaned.split("\\s+"); int[] v=new int[tokens.length]; for(int i=0;i<tokens.length;i++) v[i]=Integer.parseInt(tokens[i]); return v; } }

