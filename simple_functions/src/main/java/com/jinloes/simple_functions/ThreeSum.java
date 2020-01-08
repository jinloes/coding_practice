package com.jinloes.simple_functions;

import java.util.*;

public class ThreeSum {

    public static List<List<Integer>> threeSum(int[] nums) {
        Set<List<Integer>> threeSums = new HashSet<>();

        for (int i = 0; i < nums.length; i++) {
            Set<Integer> sums = new HashSet<>();
            int val = nums[i];
            int start = i + 1;
            for (int j = start; j < nums.length; j++) {
                int valToSearch = -1 * (val + nums[j]);
                if (sums.contains(valToSearch)) {
                    List<Integer> triple = new ArrayList<>();
                    triple.add(val);
                    triple.add(nums[j]);
                    triple.add(valToSearch);
                    triple.sort(Comparator.naturalOrder());
                    threeSums.add(triple);
                }
                sums.add(nums[j]);
            }
        }
        return new ArrayList<>(threeSums);
    }
}
