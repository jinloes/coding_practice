

import org.junit.jupiter.api.Test;

public class TreeFromPreorderAndInorderTest {

    @Test
    public void testBuildTree() {
        int[] inorder = new int[]{1, 2};
        int[] preorder = new int[]{2, 1};

        BinaryTreeNode<Integer> root = new TreeFromPreorderAndInorder().buildTree(inorder, preorder);

        System.out.println(root);
    }
}