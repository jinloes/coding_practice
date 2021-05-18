/**
 * Write a program that takes an array A and an index i into A, and rearranges the elements such that all elements
 * less than A[i] (the "pivot") appear first, followed by elements equal to the pivot,
 * followed by elements greater than the pivot.
 */
public class Partition {

    public static void partition(int[] arr, int partitionIdx) {
        if (arr == null || arr.length <= 1) {
            return;
        }

        int pivot = arr[partitionIdx];
        int lowerIdx = 0;
        int equalIdx = 0;
        int higherIdx = arr.length - 1;

        while (equalIdx <= higherIdx) {
            if (arr[equalIdx] > pivot) {
                int tmp = arr[higherIdx];
                arr[higherIdx] = arr[equalIdx];
                arr[equalIdx] = tmp;
                higherIdx--;
            } else if (arr[equalIdx] == pivot) {
                equalIdx++;
            } else {
                int tmp = arr[lowerIdx];
                arr[lowerIdx] = arr[equalIdx];
                arr[equalIdx] = tmp;
                lowerIdx++;
                equalIdx++;
            }
        }
    }
}
