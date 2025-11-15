package com.jinloes.simple_functions;

import java.util.Stack;

public class TrappingRainWater {

    public int trap(int[] height) {
        int left = 0;
        int right = height.length - 1;
        int leftMax = 0;
        int rightMax = 0;
        int area = 0;

        while(left < right) {
            int leftHeight = height[left];
            int rightHeight = height[right];

            if(leftHeight > rightHeight) {
                if(rightHeight > rightMax) {
                    rightMax = rightHeight;
                } else {
                    area += rightMax - rightHeight;
                }
                right--;
            } else {
                if(leftHeight > leftMax) {
                    leftMax = leftHeight;
                } else {
                    area += leftMax - leftHeight;
                }

                left++;
            }
        }
        return area;
    }
}
