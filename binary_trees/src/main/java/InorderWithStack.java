import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Write a nonrecursive program for computing the inorder traversal sequence for a binary tree.
 */
public class InorderWithStack {
    public static List<BinaryTreeNode<?>> traverse(BinaryTreeNode<?> root) {
        Stack<BinaryTreeNode<?>> stack = new Stack<>();
        List<BinaryTreeNode<?>> nodes = new LinkedList<>();
        BinaryTreeNode<?> current = root;


        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push((current));
                current = current.left;
            }
            current = stack.pop();
            nodes.add(current);

            current = current.right;
        }

        return nodes;
    }
}
