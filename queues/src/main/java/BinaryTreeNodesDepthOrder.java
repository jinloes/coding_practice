import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * Given a binary tree, return an array consisting of the keys at the same level. Keys should appear in the order of
 * the corresponding nodes' depths, breaking ties from left to right.
 */
public class BinaryTreeNodesDepthOrder {

    public static List<List<BinaryTreeNode<?>>> get(BinaryTreeNode<?> root) {
        Deque<BinaryTreeNode<?>> queue = new LinkedList<>();
        queue.add(root);

        List<List<BinaryTreeNode<?>>> results = new LinkedList<>();

        while (!queue.isEmpty()) {
            List<BinaryTreeNode<?>> level = new LinkedList<>();

            int queueSize = queue.size();

            for (int i = 0; i < queueSize; i++) {
                BinaryTreeNode<?> current = queue.pollFirst();
                level.add(current);
                if (current.left != null) {
                    queue.add(current.left);
                }
                if (current.right != null) {
                    queue.add(current.right);
                }
            }
            results.add(level);
        }
        return results;
    }
}
