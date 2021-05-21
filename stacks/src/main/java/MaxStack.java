import java.util.Stack;

/**
 * Design a stack that includes a max operation, in addition to push and pop. The max method should return the maximum
 * value stored in the stack.
 */
public class MaxStack {
    private Stack<StackEntry> stack;

    public MaxStack() {
        this.stack = new Stack<>();
    }

    public void push(int val) {
        int max = val;

        if (!stack.isEmpty()) {
            max = Math.max(stack.peek().max, val);
        }

        stack.push(new StackEntry(val, max));
    }

    public int pop() {
        return stack.pop().val;
    }

    public int max() {
        return stack.peek().max;
    }


    private static class StackEntry {
        int val;
        int max;

        public StackEntry(int val, int max) {
            this.val = val;
            this.max = max;
        }
    }
}
