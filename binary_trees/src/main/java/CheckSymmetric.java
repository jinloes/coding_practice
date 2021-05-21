import java.util.Objects;

/**
 * Write a program that checks whether a binary tree is symmetric.
 */
public class CheckSymmetric {
    public static boolean check(BinaryTreeNode<?> root) {
        return check(root.left, root.right);
    }

    private static boolean check(BinaryTreeNode<?> n1, BinaryTreeNode<?> n2) {
        if ((n1 == null && n2 != null)
                || (n1 != null && n2 == null)) {
            return false;
        } else if (n1 == null && n2 == null) {
            return true;
        }

        return Objects.equals(n1.data, n2.data) && check(n1.left, n2.right) && check(n1.right, n2.left);
    }
}
