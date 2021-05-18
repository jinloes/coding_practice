/**
 * Write a program which takes as input a sorted array and updates it so that all dupli¬ cates have been removed and
 * the remaining elements have been shifted left to fill the emptied indices.
 * <p>
 * Return the number of valid elements.
 */
public class DeleteDuplicates {

    public static int remove(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        if (arr.length == 1) {
            return 1;
        }

        int writeIdx = 1;

        for (int i = 1; i < arr.length; i++) {
            if (arr[i] != arr[i - 1]) {
                arr[writeIdx] = arr[i];
                writeIdx++;
            }
        }

        return writeIdx;
    }
}
