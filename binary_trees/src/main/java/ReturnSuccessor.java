import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Design an algorithm that computes the successor of a node in a binary tree. Assume that each node stores its parent.
 */
public class ReturnSuccessor {
    public static BinaryTreeNode<?> get(BinaryTreeNode<?> root, BinaryTreeNode<?> node) {
        return get(root, node, new AtomicBoolean());
    }

    private static BinaryTreeNode<?> get(BinaryTreeNode<?> root, BinaryTreeNode<?> node, AtomicBoolean shouldReturn) {
        if (root == null) {
            return null;
        }

        BinaryTreeNode<?> left = get(root.left, node, shouldReturn);

        if (left != null) {
            return left;
        }

        if (shouldReturn.get()) {
            return root;
        }

        if (Objects.equals(root, node)) {
            shouldReturn.getAndSet(true);
        }

        BinaryTreeNode<?> right = get(root.right, node, shouldReturn);

        if (right != null) {
            return right;
        }

        return null;
    }
}
