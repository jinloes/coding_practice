package com.jinloes.simple_functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Calculates the range of integers given an array of integers. Example: Input arr - [0 1 2 7 21 22
 * 1098 1099] Output - "0-2,7,21-22,1098-1099"
 */
public class ArrayRange {
    public static List<String> calculateRanges(int[] ints) {
        List<String> ranges = new ArrayList<>();
        if (ints != null && ints.length > 0) {
            int startVal = ints[0];
            int endVal = ints[0];
            for (int i = 1; i < ints.length; i++) {
                if (ints[i] - ints[i - 1] != 1) {
                    createRangeString(ranges, startVal, endVal);
                    startVal = ints[i];
                    endVal = startVal;
                } else {
                    endVal = ints[i];
                }
            }
            createRangeString(ranges, startVal, endVal);
        }
        return ranges;
    }

    private static void createRangeString(List<String> ranges, int startVal, int endVal) {
        if (startVal != endVal) {
            ranges.add(startVal + "-" + endVal);
        } else {
            ranges.add(Objects.toString(startVal));
        }
    }
}
