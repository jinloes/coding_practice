package binary_search;

class BinarySearch2DArray {
    public boolean searchMatrix(int[][] matrix, int target) {
        int rowStart = 0;
        int rowEnd = matrix.length - 1;
        int rowLength = matrix[0].length - 1;

        int rowIdx = -1;
        while (rowStart <= rowEnd) {
            int rowMid = rowStart + ((rowEnd - rowStart) / 2);
            int firstVal = matrix[rowMid][0];
            int lastVal = matrix[rowMid][rowLength];

            if (target == firstVal || target == lastVal) {
                return true;
            } else if (target > firstVal && target < lastVal) {
                rowIdx = rowMid;
                break;
            } else if (firstVal > target) {
                rowEnd = rowMid - 1;
            } else if (lastVal < target) {
                rowStart = rowMid + 1;
            }
        }

        if (rowIdx == -1) {
            return false;
        }

        int[] row = matrix[rowIdx];
        int start = 0;
        int end = rowLength;

        while (start <= end) {
            int mid = start + ((end - start) / 2);
            if (row[mid] == target) {
                return true;
            } else if (row[mid] > target) {
                end = mid - 1;
            } else {
                start = mid + 1;
            }
        }

        return false;
    }
}

