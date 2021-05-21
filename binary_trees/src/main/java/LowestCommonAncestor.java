/**
 * Design an algorithm for computing the LCA of two nodes in a binary tree in which nodes do not have a parent field.
 */
public class LowestCommonAncestor {
    public static BinaryTreeNode<?> get(BinaryTreeNode<?> root, BinaryTreeNode<?> n1, BinaryTreeNode<?> n2) {
        if (root == null) {
            return null;
        }

        if (root.data == n1.data || root.data == n2.data) {
            return root;
        }


        BinaryTreeNode<?> leftLca = get(root.left, n1, n2);
        BinaryTreeNode<?> rightLca = get(root.right, n1, n2);

        if (leftLca != null && rightLca != null) {
            return root;
        } else {
            return leftLca != null ? leftLca : rightLca;
        }
    }
}
