/**
 * Design an algorithm that takes as input a BST and two nodes, and returns the LCA of the two nodes.
 */
public class FindLCA {
    public static BSTNode<Integer> compute(BSTNode<Integer> tree, BSTNode<Integer> n1, BSTNode<Integer> n2) {
        if (tree == null) {
            return null;
        }

        if ((tree.data > n1.data && tree.data < n2.data) || (tree.data.equals(n1.data) || tree.data.equals(n2.data))) {
            return tree;
        }

        if (tree.data > n1.data) {
            return compute(tree.left, n1, n2);
        } else {
            return compute(tree.right, n1, n2);
        }
    }
}
