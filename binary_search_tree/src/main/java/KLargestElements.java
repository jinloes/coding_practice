import java.util.LinkedList;
import java.util.List;

/**
 * Write a program that takes as input a BST and an integer k, and returns the k largest elements in the BST in
 * decreasing order.
 */
public class KLargestElements {
    public static List<Integer> get(BSTNode<Integer> root, int k) {
        List<Integer> result = new LinkedList<>();

        reverseInOrder(root, k, result);

        return result;
    }

    private static void reverseInOrder(BSTNode<Integer> tree, int k, List<Integer> result) {
        if (tree == null) {
            return;
        }

        reverseInOrder(tree.right, k, result);

        if (result.size() < k) {
            result.add(tree.data);
        }

        reverseInOrder(tree.left, k, result);
    }
}
