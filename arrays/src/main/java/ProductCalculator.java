/**
 * Calculates the product of every element in an array ignoring the current index.
 */
public class ProductCalculator {
    public static int[] calculate(int[] arr) {
        if (arr == null || arr.length < 2) {
            return arr;
        }
        int[] result = new int[arr.length];
        result[0] = 1;
        // lower products
        // left to right product
        for (int i = 1; i < arr.length; i++) {
            result[i] = result[i-1] * arr[i-1];
        }
        // upper products
        int product = 1;
        // right to left product
        for (int i = arr.length-1; i >= 0; i--) {
            result[i] *= product;
            product *= arr[i];
        }
        return result;
    }
}
