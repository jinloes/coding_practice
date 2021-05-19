/**
 * Implement string/integer inter-conversion functions.
 */
public class StringIntConversion {

    public static int fromString(String str) {
        if (str == null) {
            return 0;
        }
        int result = 0;

        boolean isNegative = false;
        for (char c : str.toCharArray()) {
            if (c == '-') {
                isNegative = true;
            } else {
                result = result * 10 + Character.digit(c, 10);
            }
        }

        return isNegative ? -result : result;
    }

    public static String fromInt(int num) {
        if (num == 0) {
            return "0";
        }
        boolean isNegative = num < 0;
        StringBuilder builder = new StringBuilder();

        while (num != 0) {
            int digit = Math.abs(num % 10);
            builder.append(digit);
            num /= 10;
        }

        if (isNegative) {
            builder.append("-");
        }

        return builder.reverse()
                .toString();
    }
}
