import java.util.ArrayList;
import java.util.List;

/**
 * Write a program that takes as input a BST and an integer k, and returns the k largest elements in the BST in
 * decreasing order.
 */
public class KLargestElements {
    public static List<Integer> get(BSTNode<Integer> root, int k) {
        List<Integer> result = new ArrayList<>(k);

        reverseInOrder(root, k, result);

        return result;
    }

    private static void reverseInOrder(BSTNode<Integer> tree, int k, List<Integer> result) {
        if (tree == null || result.size() == k) {
            return;
        }

        reverseInOrder(tree.right, k, result);
        if (result.size() == k) {
            return;
        }

        result.add(tree.data);
        reverseInOrder(tree.left, k, result);
    }
}
