

/**
 * Write a function to check if a binary tree is a BST.
 */
public class BstVerifier {

    public boolean isBST(BSTNode<Integer> root) {
        if (root == null) {
            return true;
        }

        if ((root.left != null && root.left.data > root.data)
                || (root.right != null && root.right.data < root.data)) {
            return false;
        }

        return isBST(root.left) && isBST(root.right);
    }


}
