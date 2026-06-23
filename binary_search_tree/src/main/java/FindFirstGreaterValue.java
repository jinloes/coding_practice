/**
 * Write a program that takes as input a BST and a value, and returns the first key that would appear in an inorder
 * traversal which is greater than the input value.
 */
public class FindFirstGreaterValue {
    public static BSTNode<Integer> findFirstGreaterThanK(BSTNode<Integer> tree,
                                                         Integer k) {
        if (tree == null) {
            return null;
        }

        if (tree.data > k) {
            BSTNode<Integer> leftCandidate = findFirstGreaterThanK(tree.left, k);
            return leftCandidate != null ? leftCandidate : tree;
        }

        return findFirstGreaterThanK(tree.right, k);
    }
}
