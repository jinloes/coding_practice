import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BinaryTreeNodesDepthOrderTest {

    @Test
    void get() {


        BinaryTreeNode<Integer> l1l = new BinaryTreeNode<>(2);
        BinaryTreeNode<Integer> l1r = new BinaryTreeNode<>(3);

        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(1);
        root.left = l1l;
        root.right = l1r;

        assertThat(BinaryTreeNodesDepthOrder.get(root))
                .containsExactly(List.of(root), List.of(l1l, l1r));
    }
}