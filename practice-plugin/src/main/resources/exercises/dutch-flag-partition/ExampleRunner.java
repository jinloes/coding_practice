package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: an array followed by a pivot index, for example: [3, 5, 2, 3, 8] 0"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} int[] a={3,5,2,3,8}; Solution.partition(a,0); assertThat(a).containsExactlyInAnyOrder(3,5,2,3,8); assertThat(signature(a,3)).isSorted(); int[] b={4,4,4}; Solution.partition(b,1); assertThat(b).containsExactly(4,4,4); int[] c={}; Solution.partition(c,0); assertThat(c).isEmpty(); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ int[] parsed=parseInts(input); if(parsed.length==0) throw new IllegalArgumentException(); int pivotIndex=parsed[parsed.length-1]; int[] numbers=Arrays.copyOf(parsed,parsed.length-1); Solution.partition(numbers,pivotIndex); System.out.println(Arrays.toString(numbers)); }catch(RuntimeException e){ System.out.println(USAGE); } } private static int[] signature(int[] numbers,int pivot){ int[] s=new int[numbers.length]; for(int i=0;i<numbers.length;i++) s[i]=Integer.compare(numbers[i], pivot); return s; } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); String[] tokens=cleaned.split("\\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; } }

