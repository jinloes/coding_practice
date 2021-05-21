import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReturnSuccessorTest {

    @Test
    void get() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = new BinaryTreeNode<>(2);
        root.right = new BinaryTreeNode<>(3);

        root.left.left = new BinaryTreeNode<>(4);
        root.left.right = new BinaryTreeNode<>(5);

        assertThat(ReturnSuccessor.get(root, root.left.right)).isEqualTo(root);
        assertThat(ReturnSuccessor.get(root, root.left.left)).isEqualTo(root.left);
        assertThat(ReturnSuccessor.get(root, root.left)).isEqualTo(root.left.right);
        assertThat(ReturnSuccessor.get(root, root)).isEqualTo(root.right);
        assertThat(ReturnSuccessor.get(root, root.left.right)).isEqualTo(root);
    }
}