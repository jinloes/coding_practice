import java.util.List;

/**
 * Write a program which takes as input an array of digits encoding a decimal number D and updates the array to
 * represent the number D + 1. For example, if the input is (1,2,9) then you should update the array to (1,3,0).
 */
public class IncrementNumber {
    public static void increment(List<Integer> number) {
        if (number == null || number.isEmpty()) {
            return;
        }

        boolean carryOver = true;


        int idx = number.size() - 1;

        while (carryOver) {
            int digit = number.get(idx) + 1;

            if (digit == 10) {
                digit = 0;
                carryOver = true;
            } else {
                carryOver = false;
            }

            number.set(idx, digit);
            if (idx == 0 && carryOver) {
                number.add(0, 1);
                carryOver = false;
            }
            idx--;
        }
    }
}
