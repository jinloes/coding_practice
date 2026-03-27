

import java.util.Objects;

/**
 * Check if one tree is a subtree of another
 */
public class CheckSubtree {

    public boolean isSubtree(BinaryTreeNode<Integer> subtree, BinaryTreeNode<Integer> tree) {
        if (tree == null) {
            return false;
        }

        if (Objects.equals(tree.data, subtree.data) && isEqual(subtree, tree)) {
            return true;
        } else {
            return isSubtree(subtree, tree.left) || isSubtree(subtree, tree.right);
        }
    }

    private boolean isEqual(BinaryTreeNode<Integer> n1, BinaryTreeNode<Integer> n2) {
        if (n1 == null && n2 == null) {
            return true;
        } else if (n1 != null && n2 != null) {
            return Objects.equals(n1.data, n2.data)
                    && isEqual(n1.left, n2.left)
                    && isEqual(n1.right, n2.right);
        } else {
            return false;
        }
    }

}
