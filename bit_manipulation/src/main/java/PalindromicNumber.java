/**
 * Check if a number is a palindrome.
 */
public class PalindromicNumber {
    public static boolean check(int toCheck) {
        if (toCheck < 0) {
            return false;
        }

        int result = 0;
        int remaining = toCheck;

        while (remaining > 0) {
            result = result * 10 + remaining % 10;
            remaining /= 10;
        }
        return result == toCheck;
    }
}
