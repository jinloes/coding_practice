package com.jinloes.practice;

import java.util.Arrays; import java.util.List; import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: stream values followed by sample size, for example: [0, 1, 2, 3] 2"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} int[] sample = Solution.sample(List.of(0,1,2,3).iterator(),2,new Random(1)); assertThat(sample).hasSize(2); assertThat(Arrays.stream(sample).allMatch(value -> value >= 0 && value <= 3)).isTrue(); assertThat(Solution.sample(List.of(4,5).iterator(),5,new Random(1))).containsExactly(4,5); assertThat(Solution.sample(List.of(9,10).iterator(),0,new Random(1))).isEmpty(); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ int[] parsed=parseInts(input); if(parsed.length==0) throw new IllegalArgumentException(); int k=parsed[parsed.length-1]; Integer[] boxed=new Integer[parsed.length-1]; for(int i=0;i<boxed.length;i++) boxed[i]=parsed[i]; System.out.println(Arrays.toString(Solution.sample(Arrays.asList(boxed).iterator(),k,new Random(1)))); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); String[] tokens=cleaned.split("\\s+"); int[] v=new int[tokens.length]; for(int i=0;i<tokens.length;i++) v[i]=Integer.parseInt(tokens[i]); return v; } }

