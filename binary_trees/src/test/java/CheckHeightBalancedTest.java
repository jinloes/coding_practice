import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class CheckHeightBalancedTest {

    @Test
    void check() {

        BinaryTreeNode<Integer> n1l = new BinaryTreeNode<>(2);
        BinaryTreeNode<Integer> n1r = new BinaryTreeNode<>(3);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = n1l;
        root.right = n1r;

        assertThat(CheckHeightBalanced.check(root)).isTrue();

        BinaryTreeNode<Integer> n3l = new BinaryTreeNode<>(4);

        n1r.left = n3l;

        assertThat(CheckHeightBalanced.check(root)).isTrue();

        BinaryTreeNode<Integer> n4r = new BinaryTreeNode<>(3);

        n3l.right = n4r;

        assertThat(CheckHeightBalanced.check(root)).isFalse();
    }
}