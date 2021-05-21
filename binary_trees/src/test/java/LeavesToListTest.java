import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeavesToListTest {

    @Test
    void get() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = new BinaryTreeNode<>(2);
        root.right = new BinaryTreeNode<>(3);

        root.left.left = new BinaryTreeNode<>(4);
        root.left.right = new BinaryTreeNode<>(5);

        assertThat(LeavesToList.get(root)).containsExactly(root.left.left, root.left.right, root.right);
    }
}