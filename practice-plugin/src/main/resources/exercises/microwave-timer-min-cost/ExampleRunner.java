package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: startDigit moveCost pushCost targetSeconds, for example: 1 2 1 600";
    private ExampleRunner() {}
    public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.minCost(1,2,1,600)).isEqualTo(6); assertThat(Solution.minCost(0,1,2,76)).isEqualTo(6); assertThat(Solution.minCost(9,100,1,9)).isEqualTo(1); System.out.println("Examples passed: 3"); }
    private static void runInput(String input){ String[] p=input.split("\s+"); if(p.length!=4){System.out.println(USAGE);return;} try{System.out.println(Solution.minCost(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]),Integer.parseInt(p[3])));}catch(NumberFormatException exception){System.out.println(USAGE);} }
}
