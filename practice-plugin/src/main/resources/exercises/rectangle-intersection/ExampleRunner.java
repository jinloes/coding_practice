package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE = "Input: two rectangles as x y width height; x y width height, for example: 0 0 4 3; 2 1 4 4";
    private ExampleRunner() {}
    public static void main(String[] args) {
        if (args.length > 0) { runInput(String.join(" ", args).trim()); return; }
        assertThat(Solution.intersection(rect(0,0,4,3), rect(2,1,4,4))).isEqualTo(rect(2,1,2,2));
        assertThat(Solution.intersection(rect(0,0,2,2), rect(2,0,2,2))).isNull();
        assertThat(Solution.intersection(rect(-3,-2,4,4), rect(-1,-5,3,5))).isEqualTo(rect(-1,-2,2,2));
        System.out.println("Examples passed: 3");
    }
    private static void runInput(String input) {
        try { String[] h=input.split(";"); if(h.length!=2){System.out.println(USAGE);return;} System.out.println(Solution.intersection(parse(h[0]), parse(h[1]))); }
        catch (NumberFormatException exception) { System.out.println(USAGE); }
    }
    private static Solution.Rectangle parse(String text){ String[] p=text.trim().split("\s+"); if(p.length!=4) throw new NumberFormatException(); return rect(Integer.parseInt(p[0]),Integer.parseInt(p[1]),Integer.parseInt(p[2]),Integer.parseInt(p[3])); }
    private static Solution.Rectangle rect(int x,int y,int w,int h){ return new Solution.Rectangle(x,y,w,h); }
}
