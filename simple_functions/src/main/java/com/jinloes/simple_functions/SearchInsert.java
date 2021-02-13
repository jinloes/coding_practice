package com.jinloes.simple_functions;

public class SearchInsert {
    public int searchInsert(int[] nums, int target) {
        if (nums.length == 0) {
            return 0;
        }
        int idx = search(nums, target);

        if (nums[idx] == target) {
            return idx;
        } else if (nums[idx] > target) {
            return idx;
        } else {
            return idx + 1;
        }
    }

    private int search(int[] nums, int target) {
        int start = 0;
        int end = nums.length - 1;

        while (start <= end) {
            int mid = start + ((end - start) / 2);
            int midValue = nums[mid];

            if (midValue == target) {
                return mid;
            } else if (midValue < target) {
                if (mid + 1 < nums.length && nums[mid + 1] > target) {
                    return mid + 1;
                } else if (mid == nums.length - 1) {
                    return mid;
                }
                start = mid + 1;
            } else {
                if (mid - 1 > 0 && nums[mid - 1] < target) {
                    return mid - 1;
                } else if (mid == 0) {
                    return mid;
                }
                end = mid - 1;
            }
        }
        return -1;
    }
}
