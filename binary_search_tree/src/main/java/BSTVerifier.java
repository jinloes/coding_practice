/**
 * Write a function to check if a binary tree is a BST.
 */
public class BSTVerifier {

    public boolean isBST(BSTNode<Integer> root) {
        return isBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean isBST(BSTNode<Integer> node, long min, long max) {
        if (node == null) {
            return true;
        }

        if (node.data < min || node.data > max) {
            return false;
        }

        return isBST(node.left, min, node.data)
                && isBST(node.right, node.data, max);
    }
}
