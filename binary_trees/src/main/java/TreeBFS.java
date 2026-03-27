

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

/**
 * BFS adds each nodes children to a queue to visit in level order.
 */
public class TreeBFS {
    public static <T> boolean search(NaryTreeNode<T> root, T toSearch) {
        Queue<NaryTreeNode<?>> nodeQueue = new ArrayDeque<>();
        nodeQueue.add(root);

        while (!nodeQueue.isEmpty()) {
            NaryTreeNode<?> current = nodeQueue.poll();
            if (Objects.equals(current.getValue(), toSearch)) {
                return true;
            }
            nodeQueue.addAll(current.getChildren());
        }

        return false;
    }
}
