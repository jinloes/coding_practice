package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.List;

public class PascalTriangle {

    public List<List<Integer>> generate(int numRows) {
        List<List<Integer>> triangle = new ArrayList<>();
        if (numRows <= 0) {
            return triangle;
        }

        for (int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            for (int j = 0; j < i + 1; j++) {
                if (j - 1 < 0 || i - 1 < 0 || i == j) {
                    row.add(1);
                } else {
                    int sum = triangle.get(i - 1).get(j) + triangle.get(i - 1).get(j - 1);
                    row.add(sum);
                }
            }
            triangle.add(row);
        }
        return triangle;
    }
}
