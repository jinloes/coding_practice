/**
 * Write a program that takes as input the root of a binary tree and checks whether the tree is height-balanced.
 */
public class CheckHeightBalanced {

    public static boolean check(BinaryTreeNode<?> root) {

        int leftHeight = getHeight(root.left);
        int rightHeight = getHeight(root.right);

        return Math.abs(leftHeight - rightHeight) <= 1;
    }

    private static int getHeight(BinaryTreeNode<?> root) {
        if (root == null) {
            return 0;
        }
        return 1 + Math.max(getHeight(root.left), getHeight(root.right));
    }
}
