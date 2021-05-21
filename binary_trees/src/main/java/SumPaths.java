/**
 * Design an algorithm to compute the sum of the binary numbers represented by the root-to-leaf paths.
 */
public class SumPaths {
    public static int compute(BinaryTreeNode<Integer> root) {
        return compute(root, 0);
    }

    private static int compute(BinaryTreeNode<Integer> node, int sum) {
        if (node == null) {
            return 0;
        }

        sum = sum * 2 + node.data;

        if (node.left == null && node.right == null) {
            return sum;
        }

        return compute(node.left, sum) + compute(node.right, sum);
    }
}
