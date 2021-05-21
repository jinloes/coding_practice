import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CheckSymmetricTest {

    @Test
    void check() {
        BinaryTreeNode<Integer> n1l = new BinaryTreeNode<>(2);
        BinaryTreeNode<Integer> n1r = new BinaryTreeNode<>(2);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = n1l;
        root.right = n1r;

        assertThat(CheckSymmetric.check(root)).isTrue();

        BinaryTreeNode<Integer> n2al = new BinaryTreeNode<>(3);
        n1l.left = n2al;

        assertThat(CheckSymmetric.check(root)).isFalse();

        BinaryTreeNode<Integer> n2br = new BinaryTreeNode<>(3);
        n1r.right = n2br;

        assertThat(CheckSymmetric.check(root)).isTrue();

        BinaryTreeNode<Integer> n3ar = new BinaryTreeNode<>(4);
        n2al.right = n3ar;

        assertThat(CheckSymmetric.check(root)).isFalse();

        BinaryTreeNode<Integer> n3dl = new BinaryTreeNode<>(4);
        n2br.left = n3dl;

        assertThat(CheckSymmetric.check(root)).isTrue();
    }
}