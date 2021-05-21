import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SumPathsTest {

    @Test
    void compute() {
        BinaryTreeNode<Integer> n1l = new BinaryTreeNode<>(0);
        BinaryTreeNode<Integer> n1r = new BinaryTreeNode<>(0);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = n1l;
        root.right = n1r;

        assertThat(SumPaths.compute(root)).isEqualTo(4);

        BinaryTreeNode<Integer> n0al = new BinaryTreeNode<>(1);

        n1l.left = n0al;

        assertThat(SumPaths.compute(root)).isEqualTo(7);
    }
}