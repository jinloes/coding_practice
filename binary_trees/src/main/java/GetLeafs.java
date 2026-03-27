

import java.util.ArrayList;
import java.util.List;

/**
 * Given a binary tree, we need to write a program to print all leaf nodes of the given binary tree from left to right.
 * That is, the nodes should be printed in the order they appear from left to right in the given tree.
 */
public class GetLeafs {

    public List<Integer> getLeafs(BinaryTreeNode<Integer> root) {
        List<Integer> leafs = new ArrayList<>();

        if (root == null) {
            return leafs;
        }

        doGetLeafs(root, leafs);

        return leafs;
    }

    private void doGetLeafs(BinaryTreeNode<Integer> root, List<Integer> leafs) {
        if (root == null) {
            return;
        }

        if (root.left == null && root.right == null) {
            leafs.add(root.data);
        }

        doGetLeafs(root.left, leafs);
        doGetLeafs(root.right, leafs);
    }
}
