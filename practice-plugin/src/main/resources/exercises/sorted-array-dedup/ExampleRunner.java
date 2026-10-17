package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: a sorted integer array, for example: [1, 1, 2]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertExample(new int[]{1,1,2},2,new int[]{1,2}); assertExample(new int[]{-3,-3,-1,0,0,5},4,new int[]{-3,-1,0,5}); assertExample(new int[0],0,new int[0]); System.out.println("Examples passed: 3"); } private static void assertExample(int[] n,int c,int[] p){ int count=Solution.deduplicate(n); assertThat(count).isEqualTo(c); assertThat(Arrays.copyOf(n,count)).containsExactly(p); } private static void runInput(String input){ try{ int[] numbers=parseInts(input); int count=Solution.deduplicate(numbers); System.out.println("count "+count+", prefix "+Arrays.toString(Arrays.copyOf(numbers,count))); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] tokens=cleaned.split("\\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; } }

