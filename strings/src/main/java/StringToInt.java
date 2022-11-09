/**
 * Write two conversion routines. The first routine converts a string to a signed
 * integer. You may assume that the string contains only digits and the minus
 * character ('-'), that it is a properly formatted integer number, and that the
 * number is within the range of an int type. The second routine converts a
 * signed integer stored as an int back to a string.
 */
public class StringToInt {
    public static int convertToInt(String str) {
        char[] chars = str.toCharArray();

        int result = 0;
        boolean isNegative = chars[0] == '-';
        int start = isNegative ? 1 : 0;

        for (int i = start; i < chars.length; i++) {
            result *= 10;
            result += chars[i] - '0';
        }

        return isNegative ? -result : result;
    }

    public static String intToString(int val) {
        boolean isNegative = val < 0;
        val = Math.abs(val);
        StringBuilder result = new StringBuilder();

        while (val > 0) {
            int remainder = val % 10;
            result.append(remainder);
            val /= 10;
        }

        if (isNegative) {
            result.append('-');
        }

        return result.reverse().toString();
    }
}
