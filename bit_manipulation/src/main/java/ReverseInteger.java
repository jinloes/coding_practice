/**
 * Write a function that performs base conversion
 */
public class ReverseInteger {
    public static int reverse(int toReverse) {
        int result = 0;
        int remaining = Math.abs(toReverse);

        while (remaining > 0) {
            result *= 10;
            int val = remaining % 10;
            result += val;
            remaining /= 10;
        }

        return toReverse < 0 ? -result : result;
    }
}
