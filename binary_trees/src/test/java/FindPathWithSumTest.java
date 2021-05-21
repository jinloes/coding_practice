import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FindPathWithSumTest {

    @Test
    void exists() {
        BinaryTreeNode<Integer> n1l = new BinaryTreeNode<>(4);
        BinaryTreeNode<Integer> n1r = new BinaryTreeNode<>(3);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = n1l;
        root.right = n1r;

        assertThat(FindPathWithSum.exists(root, 5)).isTrue();
        assertThat(FindPathWithSum.exists(root, 4)).isTrue();
        assertThat(FindPathWithSum.exists(root, 1)).isFalse();
    }
}