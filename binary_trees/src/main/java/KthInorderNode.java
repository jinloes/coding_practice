import java.util.concurrent.atomic.AtomicInteger;

/**
 * Write a program that efficiently computes the kth node appearing in an inorder traversal. Assume that each node
 * stores the number of nodes in the subtree rooted at that node.
 */
public class KthInorderNode {
    public static BinaryTreeNode<?> get(BinaryTreeNode<?> root, int k) {
        return get(root, k, new AtomicInteger());
    }

    private static BinaryTreeNode<?> get(BinaryTreeNode<?> node, int k, AtomicInteger count) {
        if (node == null) {
            return null;
        }

        BinaryTreeNode<?> leftTrav = get(node.left, k, count);


        if (leftTrav != null) {
            return leftTrav;
        }

        int val = count.incrementAndGet();

        if (val == k) {
            return node;
        }

        BinaryTreeNode<?> rightTrav = get(node.right, k, count);

        if (rightTrav != null) {
            return rightTrav;
        }

        return null;
    }

}
