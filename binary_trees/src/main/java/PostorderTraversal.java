

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Postorder visits tree nodes in Left, Right, Root order
 */
public class PostorderTraversal {

    /**
     * Binary traverse
     *
     * @param root
     * @param <T>
     * @return
     */
    public static <T> List<T> traverse(NaryTreeNode<T> root) {
        List<T> traversal = Lists.newArrayList();
        List<NaryTreeNode<T>> children = root.getChildren();
        if (children.size() > 0) {
            List<T> childTraversal = traverse(root.getChildren().get(0));
            traversal.addAll(childTraversal);
        }
        if (children.size() > 1) {
            List<T> childTraversal = traverse(root.getChildren().get(1));
            traversal.addAll(childTraversal);
        }
        traversal.add(root.getValue());
        return traversal;
    }


    /**
     * Nary traverse.
     *
     * @param root
     * @param <T>
     * @return
     */
    public static <T> List<T> traverseNary(NaryTreeNode<T> root) {
        List<NaryTreeNode<T>> children = root.getChildren();
        if (children.isEmpty()) {
            return Lists.newArrayList(root.getValue());
        }
        List<T> traversal = root.getChildren()
                .stream()
                .flatMap(child -> traverseNary(child).stream())
                .collect(Collectors.toList());
        traversal.add(root.getValue());
        return traversal;
    }


}
