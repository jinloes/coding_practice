package binary_search;

class SearchRange {
    public int[] searchRange(int[] nums, int target) {
        int startIdx = search(nums, target, true);
        int endIdx = search(nums, target, false);

        return new int[]{startIdx, endIdx};
    }

    private int search(int[] nums, int target, boolean first) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > target) {
                right = mid - 1;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                if (first) {
                    if (mid == left || nums[mid - 1] != target) {
                        return mid;
                    }
                    right = mid - 1;
                } else {
                    if (mid == right || nums[mid + 1] != target) {
                        return mid;
                    }
                    left = mid + 1;
                }

            }
        }

        return -1;
    }
}
