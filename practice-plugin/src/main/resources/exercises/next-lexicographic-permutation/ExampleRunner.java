package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: an integer array, for example: [1, 2, 3]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} int[] a={1,2,3}; assertThat(Solution.nextPermutation(a)).isTrue(); assertThat(a).containsExactly(1,3,2); int[] b={3,2,1}; assertThat(Solution.nextPermutation(b)).isFalse(); assertThat(b).containsExactly(1,2,3); int[] c={1,1,5}; assertThat(Solution.nextPermutation(c)).isTrue(); assertThat(c).containsExactly(1,5,1); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ int[] values=parseInts(input); boolean advanced=Solution.nextPermutation(values); System.out.println(advanced+" "+Arrays.toString(values)); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] tokens=cleaned.split("\\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; } }

