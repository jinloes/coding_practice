package com.jinloes.practice;

public class Solution {
    public record Rectangle(int x, int y, int width, int height) {}
    public static Rectangle intersection(Rectangle first, Rectangle second) {
        long left=Math.max(first.x(),second.x()), bottom=Math.max(first.y(),second.y());
        long right=Math.min((long)first.x()+first.width(),(long)second.x()+second.width());
        long top=Math.min((long)first.y()+first.height(),(long)second.y()+second.height());
        if(right<=left || top<=bottom) return null;
        return new Rectangle((int)left,(int)bottom,(int)(right-left),(int)(top-bottom));
    }
}
