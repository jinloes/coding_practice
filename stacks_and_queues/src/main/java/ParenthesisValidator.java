import java.util.Stack;

/**
 * Write a program that tests if a string made up of the characters '(', ')', '[', and"}' is well-formed.
 */
public class ParenthesisValidator {

    public static boolean validate(String toValidate) {
        Stack<Character> stack = new Stack<>();

        for (Character c : toValidate.toCharArray()) {
            if (c == '(' || c == '[' || c == '{') {
                stack.push(c);
            } else if (c == ')' && !isValid(stack, '(')) {
                return false;
            } else if (c == ']' && !isValid(stack, '[')) {
                return false;
            } else if (c == '}' && !isValid(stack, '{')) {
                return false;
            }
        }

        return stack.isEmpty();
    }

    private static boolean isValid(Stack<Character> stack, char validChar) {
        if (stack.isEmpty()) {
            return false;
        }

        return validChar == stack.pop();
    }
}
