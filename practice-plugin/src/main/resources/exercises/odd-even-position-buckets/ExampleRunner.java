package com.jinloes.practice;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: linked-list values, for example: [10, 11, 12, 13, 14]"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.bucketByPosition(list(10,11,12,13,14))).containsExactly(List.of(10,12,14), List.of(11,13)); assertThat(Solution.bucketByPosition(list(5))).containsExactly(List.of(5), List.of()); assertThat(Solution.bucketByPosition(null)).containsExactly(List.of(), List.of()); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ System.out.println(Solution.bucketByPosition(list(parseInts(input)))); }catch(RuntimeException e){ System.out.println(USAGE); } } private static Solution.Node list(int... values){ Solution.Node head=null; for(int i=values.length-1;i>=0;i--) head=new Solution.Node(values[i],head); return head; } private static int[] parseInts(String input){ String cleaned=input.replace('[',' ').replace(']',' ').replace(',',' ').trim(); if(cleaned.isEmpty()) return new int[0]; String[] tokens=cleaned.split("\\s+"); int[] values=new int[tokens.length]; for(int i=0;i<tokens.length;i++) values[i]=Integer.parseInt(tokens[i]); return values; } }

