/**
 * Implement a function which takes as input an array and a key and updates the array so that all occurrences of
 * the input key have been removed and the remaining elements have been shifted left to fill the emptied indices.
 * Return the number of remaining elements.
 * There are no requirements as to the values stored beyond the last valid element.
 */
public class DeleteValue {

    public static int delete(int[] arr, int value) {
        if (arr == null || arr.length == 0) {
            return 0;
        }

        int writeIdx = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != value) {
                arr[writeIdx] = arr[i];
                writeIdx++;
            }
        }

        return writeIdx;
    }
}
