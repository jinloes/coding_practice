

import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;

public class ArrayToBST {
    public TreeNode<Integer> toBST(int[] arr) {
        if (arr == null || arr.length == 0) {
            return null;
        }

        return toBST(arr, 0, arr.length - 1);
    }

    private TreeNode<Integer> toBST(int[] arr, int start, int end) {
        if (start > end) {
            return null;
        }
        int mid = start + ((end - start) / 2);
        TreeNode<Integer> root = new ArrayMultiTreeNode<>(arr[mid]);
        root.add(toBST(arr, start, mid - 1));
        root.add(toBST(arr, mid + 1, end));
        return root;
    }

}
