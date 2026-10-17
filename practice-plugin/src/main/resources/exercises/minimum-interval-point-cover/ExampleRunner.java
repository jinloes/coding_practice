package com.jinloes.practice;

import java.util.*;
import java.util.regex.*;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: closed intervals as [start,end] pairs, for example: [1,3] [2,5] [6,7]";
    private static final Pattern PAIR = Pattern.compile("\\[\\s*(-?\\d+)\\s*,\\s*(-?\\d+)\\s*]");
    private ExampleRunner() {}
    public static void main(String[] args){ if (args.length > 0) {runInput(String.join(" ",args).trim());return;} assertThat(Solution.minimumCoverPoints(intervals(1,3,2,5,6,7))).containsExactly(3,7); assertThat(Solution.minimumCoverPoints(intervals(1,2,2,3,3,4))).containsExactly(2,4); assertThat(Solution.minimumCoverPoints(new Solution.Interval[0])).isEmpty(); System.out.println("Examples passed: 3"); }
    private static void runInput(String input){ try{System.out.println(Arrays.toString(Solution.minimumCoverPoints(parse(input))));}catch(NumberFormatException exception){System.out.println(USAGE);} }
    private static Solution.Interval[] parse(String input){Matcher m=PAIR.matcher(input); List<Solution.Interval> list=new ArrayList<>(); while(m.find()) list.add(new Solution.Interval(Integer.parseInt(m.group(1)),Integer.parseInt(m.group(2)))); if(list.isEmpty()) throw new NumberFormatException(); return list.toArray(Solution.Interval[]::new);}
    private static Solution.Interval[] intervals(int... v){Solution.Interval[] a=new Solution.Interval[v.length/2]; for(int i=0;i<a.length;i++)a[i]=new Solution.Interval(v[2*i],v[2*i+1]); return a;}
}
