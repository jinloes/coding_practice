import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BstVerifierTest {
    private BstVerifier bstVerifier;

    @BeforeEach
    void setUp() {
        bstVerifier = new BstVerifier();
    }

    @Test
    void testVerify() {
        BSTNode<Integer> root = new BSTNode<>(4,
                new BSTNode<>(2, new BSTNode<>(1), new BSTNode<>(3)),
                new BSTNode<>(5));
        assertThat(bstVerifier.isBST(root)).isTrue();
    }

    @Test
    void testVerifyFalse() {
        BSTNode<Integer> threeLeft = new BSTNode<>(4, null, null);
        BSTNode<Integer> oneLeft = new BSTNode<>(2, null, null);
        BSTNode<Integer> oneRight = new BSTNode<>(6, threeLeft, null);
        BSTNode<Integer> root = new BSTNode<>(1, oneLeft, oneRight);
        assertThat(bstVerifier.isBST(root)).isFalse();
    }
}
