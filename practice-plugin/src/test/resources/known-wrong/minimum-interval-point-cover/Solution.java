package com.jinloes.practice;
import java.util.*;
public class Solution { public record Interval(int start,int end) {} public static int[] minimumCoverPoints(Interval[] intervals){ return Arrays.stream(intervals).sorted(Comparator.comparingInt(Interval::start)).mapToInt(Interval::end).toArray(); } }
