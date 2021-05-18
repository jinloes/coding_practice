/**
 * Partition an array into even and odds.
 */
public class EvenOddArray {
    public static void partition(int[] arr) {
        if (arr == null || arr.length == 1) {
            return;
        }
        int evenIdx = 0;
        int oddIdx = arr.length - 1;


        while (evenIdx < oddIdx) {
            if (arr[evenIdx] % 2 == 0) {
                evenIdx++;
            } else {
                int temp = arr[oddIdx];
                arr[oddIdx] = arr[evenIdx];
                arr[evenIdx] = temp;
                oddIdx--;
            }
        }
    }
}
