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

        BSTNode<Integer> candidate = null;

        if (tree.data > k) {
            candidate = tree;
            BSTNode<Integer> next = findFirstGreaterThanK(tree.left, k);
            if (next != null) {
                candidate = next;
            }
        } else {
            BSTNode<Integer> next = findFirstGreaterThanK(tree.right, k);
            if (next != null) {
                candidate = next;
            }
        }

        return candidate;
    }
}
