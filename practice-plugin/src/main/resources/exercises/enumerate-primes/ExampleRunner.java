package com.jinloes.practice;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner { private static final String USAGE="Input: an inclusive upper bound, for example: 20"; private ExampleRunner(){} public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.enumeratePrimes(1)).containsExactly(); assertThat(Solution.enumeratePrimes(10)).containsExactly(2,3,5,7); assertThat(Solution.enumeratePrimes(20)).containsExactly(2,3,5,7,11,13,17,19); System.out.println("Examples passed: 3"); } private static void runInput(String input){ try{ System.out.println(Arrays.toString(Solution.enumeratePrimes(Integer.parseInt(input.strip())))); }catch(RuntimeException e){ System.out.println(USAGE); } } }

