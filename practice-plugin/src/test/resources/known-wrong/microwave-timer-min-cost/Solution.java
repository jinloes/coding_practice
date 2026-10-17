package com.jinloes.practice;
public class Solution { public static int minCost(int startDigit,int moveCost,int pushCost,int targetSeconds){ String b=Integer.toString((targetSeconds/60)*100 + targetSeconds%60); int cost=0,f=startDigit; for(int i=0;i<b.length();i++){int d=b.charAt(i)-'0'; if(f!=d){cost+=moveCost; f=d;} cost+=pushCost;} return cost; } }
