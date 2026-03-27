import java.util.Stack;
import java.util.function.BiFunction;

/**
 * Write a program that takes an arithmetical expression in RPN and returns the number that the expression evaluates to.
 */
public class RPNEvaluator {

    public static int eval(String rpn) {
        String[] parts = rpn.split(",");
        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < parts.length; i++) {
            String token = parts[i];
            if ("-".equals(token)) {
                doMath((num1, num2) -> num1 - num2, stack);
            } else if ("x".equals(token)) {
                doMath((num1, num2) -> num1 * num2, stack);
            } else if ("/".equals(token)) {
                doMath((num1, num2) -> num1 / num2, stack);
            } else if ("+".equals(token)) {
                doMath((num1, num2) -> num1 + num2, stack);
            } else {
                stack.push(Integer.parseInt(token));
            }
        }
        return stack.pop();
    }

    private static void doMath(BiFunction<Integer, Integer, Integer> function, Stack<Integer> stack) {
        int num1 = stack.pop();
        int num2 = stack.pop();

        int result = function.apply(num1, num2);
        stack.push(result);
    }

}
