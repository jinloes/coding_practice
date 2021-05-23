/**
 * Write a program that takes as input a binary tree and checks if the tree satisfies the
 * BST property.
 */
public class BSTVerifier {
    public static boolean isValid(BSTNode<Integer> root) {
        return isValid(root, Integer.MIN_VALUE, Integer.MAX_VALUE);

    }

    private static boolean isValid(BSTNode<Integer> root, int lowerBound, int upperBound) {
        if (root == null) {
            return true;
        }

        if (root.data > upperBound || root.data < lowerBound) {
            return false;
        }

        return isValid(root.left, lowerBound, root.data) && isValid(root.right, root.data, upperBound);
    }
}
