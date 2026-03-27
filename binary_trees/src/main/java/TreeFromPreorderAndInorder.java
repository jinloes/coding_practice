

import java.util.HashMap;
import java.util.Map;

public class TreeFromPreorderAndInorder {
    private int preIdx;
    private Map<Integer, Integer> inOrderMap;

    public BinaryTreeNode<Integer> buildTree(int[] preorder, int[] inorder) {
        preIdx = 0;
        inOrderMap = new HashMap<>();

        for (int i = 0; i < inorder.length; i++) {
            inOrderMap.put(inorder[i], i);
        }
        return helper(preorder, inorder, 0, preorder.length - 1);
    }

    private BinaryTreeNode<Integer> helper(int[] preorder, int[] inorder, int inStart, int inEnd) {
        if (inStart > inEnd) {
            return null;
        }
        int val = preorder[preIdx++];
        BinaryTreeNode<Integer> node = new BinaryTreeNode<>(val);
        if (inStart == inEnd) {
            return node;
        }
        int inIdx = inOrderMap.get(val);
        node.left = helper(preorder, inorder, inStart, inIdx - 1);
        node.right = helper(preorder, inorder, inIdx + 1, inEnd);

        return node;
    }
}
