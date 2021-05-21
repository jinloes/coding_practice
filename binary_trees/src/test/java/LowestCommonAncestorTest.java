import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LowestCommonAncestorTest {

    @Test
    void get() {
        BinaryTreeNode<Integer> n1l = new BinaryTreeNode<>(2);
        BinaryTreeNode<Integer> n1r = new BinaryTreeNode<>(3);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);

        root.left = n1l;
        root.right = n1r;

        assertThat(LowestCommonAncestor.get(root, n1l, n1r)).isEqualTo(root);

        BinaryTreeNode<Integer> n2l = new BinaryTreeNode<>(4);
        n1l.left = n2l;

        assertThat(LowestCommonAncestor.get(root, n1l, n2l)).isEqualTo(n1l);
        assertThat(LowestCommonAncestor.get(root, n1r, n2l)).isEqualTo(root);

        BinaryTreeNode<Integer> n4r = new BinaryTreeNode<>(5);
        n2l.right = n4r;

        assertThat(LowestCommonAncestor.get(root, n1l, n4r)).isEqualTo(n1l);
    }
}