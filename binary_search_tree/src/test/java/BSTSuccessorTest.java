import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BSTSuccessorTest {
    private BSTSuccessor bstSuccessor;

    @BeforeEach
    void setUp() {
        bstSuccessor = new BSTSuccessor();
    }

    @Test
    void getSuccessor() {
        BSTNode<Integer> n1 = new BSTNode<>(1);
        BSTNode<Integer> n2 = new BSTNode<>(2);
        BSTNode<Integer> n3 = new BSTNode<>(3);
        BSTNode<Integer> n4 = new BSTNode<>(4);
        BSTNode<Integer> n5 = new BSTNode<>(5);
        BSTNode<Integer> n5_2 = new BSTNode<>(5);
        BSTNode<Integer> n9 = new BSTNode<>(9);

        n4.left = n2;
        n4.right = n5;

        n2.parent = n4;
        n2.left = n1;
        n2.right = n3;

        n1.parent = n2;
        n3.parent = n2;

        n5.parent = n4;
        n5.left = n5_2;
        n5.right = n9;

        n5_2.parent = n5;
        n9.parent = n5;

        assertThat(bstSuccessor.getSuccessor(n1)).isEqualTo(n2);
        assertThat(bstSuccessor.getSuccessor(n3)).isEqualTo(n4);
        assertThat(bstSuccessor.getSuccessor(n5)).isEqualTo(n9);
        assertThat(bstSuccessor.getSuccessor(n9)).isNull();
        assertThat(bstSuccessor.getSuccessor(n4)).isEqualTo(n5_2);
    }
}
