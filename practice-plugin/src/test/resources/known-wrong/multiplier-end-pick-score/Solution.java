package com.jinloes.practice;
public class Solution { public static int maximumScore(int[] nums, int[] multipliers) { int l=0,r=nums.length-1,s=0; for(int m: multipliers) s += m * (nums[l] >= nums[r] ? nums[l++] : nums[r--]); return s; } }
