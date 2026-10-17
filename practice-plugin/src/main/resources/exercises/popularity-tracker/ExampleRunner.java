package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: operations separated by commas, for example: inc 10, inc 20, inc 10, most";
    private ExampleRunner() {}
    public static void main(String[] args) { if (args.length > 0) { runInput(String.join(" ", args).trim()); return; } Solution t=new Solution(); t.increasePopularity(10); t.increasePopularity(20); t.increasePopularity(10); assertThat(t.mostPopular()).isEqualTo(10); Solution u=new Solution(); u.increasePopularity(4); u.decreasePopularity(4); assertThat(u.mostPopular()).isEqualTo(-1); System.out.println("Examples passed: 2"); }
    private static void runInput(String input) { Solution t=new Solution(); try { for(String op:input.split(",")){String[] p=op.trim().split("\s+"); if(p.length==2&&p[0].equals("inc")) t.increasePopularity(Integer.parseInt(p[1])); else if(p.length==2&&p[0].equals("dec")) t.decreasePopularity(Integer.parseInt(p[1])); else if(p.length==1&&p[0].equals("most")) System.out.println(t.mostPopular()); else {System.out.println("Unknown operation. "+USAGE); return;}} } catch(RuntimeException e){System.out.println("Could not run operations. "+USAGE);} }
}
