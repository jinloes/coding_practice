import java.util.LinkedList;
import java.util.List;

/**
 * Given a binary tree, compute a linked list from the leaves of the binary tree. The leaves should appear in
 * left-to-right order.
 */
public class LeavesToList {
    public static List<BinaryTreeNode<?>> get(BinaryTreeNode<?> root) {
        List<BinaryTreeNode<?>> leaves = new LinkedList<>();

        get(root, leaves);

        return leaves;
    }

    private static void get(BinaryTreeNode<?> root, List<BinaryTreeNode<?>> leaves) {
        if (root == null) {
            return;
        }
        if (root.left == null && root.right == null) {
            leaves.add(root);
        }

        get(root.left, leaves);
        get(root.right, leaves);
    }
}
