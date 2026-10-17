package com.jinloes.practice;

import java.util.List;

public class Solution { public static List<String> expand(String pattern) { return List.of(pattern.replace('?', '0')); } }
