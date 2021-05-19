/**
 * Implement a function which takes as input a string s and returns true if s is a palindromic string.
 */
public class IsPalindrome {

    public static boolean check(String str) {
        int leftIdx = 0;
        int rightIdx = str.length() - 1;
        while (leftIdx < rightIdx) {
            if (!Character.isAlphabetic(str.charAt(leftIdx)) && !Character.isDigit(str.charAt(leftIdx))) {
                leftIdx++;
            } else if (!Character.isAlphabetic(str.charAt(rightIdx)) && !Character.isDigit(str.charAt(rightIdx))) {
                rightIdx--;
            } else if (Character.toLowerCase(str.charAt(leftIdx)) != Character.toLowerCase(str.charAt(rightIdx))) {
                return false;
            } else {
                leftIdx++;
                rightIdx--;
            }
        }

        return true;
    }
}
