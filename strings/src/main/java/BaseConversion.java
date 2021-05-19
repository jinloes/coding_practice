/**
 * Write a program that performs base conversion. The input is a string, an integer b1,and another integer b2.
 * The string represents be an integer in base b2.
 * The output should be the string representing the integer in base b2.
 */
public class BaseConversion {

    public static String convert(String numStr, int b1, int b2) {
        int num = 0;

        boolean isNegative = false;

        for (char c : numStr.toCharArray()) {
            if (c == '-') {
                isNegative = true;
            } else {
                num = num * b1 + Character.digit(c, 10);
            }
        }

        num = isNegative ? -num : num;

        StringBuilder result = new StringBuilder();

        while (num != 0) {
            int digit = Math.abs(num % b2);
            Character c = Character.forDigit(digit, b2);
            result.append(c);
            num /= b2;
        }

        if (isNegative) {
            result.append('-');
        }


        return result.reverse().toString();

    }

}
