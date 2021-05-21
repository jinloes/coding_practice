import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class KthInorderNodeTest {

    @Test
    void get() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = new BinaryTreeNode<>(2);
        root.right = new BinaryTreeNode<>(3);

        root.left.left = new BinaryTreeNode<>(4);
        root.left.right = new BinaryTreeNode<>(5);

        assertThat(KthInorderNode.get(root, 1)).isEqualTo(root.left.left);
        assertThat(KthInorderNode.get(root, 2)).isEqualTo(root.left);
        assertThat(KthInorderNode.get(root, 3)).isEqualTo(root.left.right);
        assertThat(KthInorderNode.get(root, 4)).isEqualTo(root);
        assertThat(KthInorderNode.get(root, 5)).isEqualTo(root.right);
    }
}