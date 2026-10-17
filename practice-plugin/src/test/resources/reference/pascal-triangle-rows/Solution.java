package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

public class Solution {
    public static List<List<Integer>> rows(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();
        for (int r = 0; r < numRows; r++) {
            List<Integer> row = new ArrayList<>();
            for (int c = 0; c <= r; c++) row.add(c == 0 || c == r ? 1 : triangle.get(r - 1).get(c - 1) + triangle.get(r - 1).get(c));
            triangle.add(row);
        }
        return triangle;
    }
}
