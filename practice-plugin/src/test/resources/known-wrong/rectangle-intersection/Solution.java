package com.jinloes.practice;
public class Solution { public record Rectangle(int x,int y,int width,int height) {} public static Rectangle intersection(Rectangle first, Rectangle second) { return first.x() <= second.x() && first.y() <= second.y() ? second : null; } }
