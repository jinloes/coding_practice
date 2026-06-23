/**
 * Design an algorithm that takes as input a BST and two nodes, and returns the LCA of the two nodes.
 */
public class FindLCA {
    public static BSTNode<Integer> compute(BSTNode<Integer> tree, BSTNode<Integer> n1, BSTNode<Integer> n2) {
        if (tree == null) {
            return null;
        }

        int lower = Math.min(n1.data, n2.data);
        int upper = Math.max(n1.data, n2.data);

        if (tree.data >= lower && tree.data <= upper) {
            return tree;
        }

        if (tree.data > upper) {
            return compute(tree.left, n1, n2);
        }

        return compute(tree.right, n1, n2);
    }
}
