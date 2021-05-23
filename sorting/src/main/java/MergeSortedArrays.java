import java.util.List;

/**
 * Write a program which takes as input two sorted arrays of integers, and updates the first to the combined entries
 * of the two arrays in sorted order. Assume the first array has enough empty entries at its end to hold the result.
 */
public class MergeSortedArrays {
    public static void merge(List<Integer> list1, int m, List<Integer> list2, int n) {
        int i = m - 1;
        int j = n - 1;

        int writeIdx = m + n - 1;

        while (i >= 0 && j >= 0) {
            Integer val1 = list1.get(i);
            Integer val2 = list2.get(j);

            if (val1 > val2) {
                list1.set(writeIdx, val1);
                writeIdx--;
                i--;
            } else {
                list1.set(writeIdx, val2);
                writeIdx--;
                j--;
            }
        }

        while (j >= 0) {
            list1.set(writeIdx, list2.get(j));
            writeIdx--;
            j--;
        }
    }

}
