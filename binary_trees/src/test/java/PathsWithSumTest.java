

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PathsWithSumTest {
    private PathsWithSum pathsWithSum;

    @BeforeEach
    public void setUp() throws Exception {
        pathsWithSum = new PathsWithSum();
    }

    @Test
    public void countSum() {
        BinaryTreeNode<Integer> root = new BinaryTreeNode<>(5,
                new BinaryTreeNode<>(2, new BinaryTreeNode<>(3, new BinaryTreeNode<>(-1, new BinaryTreeNode<>(1), null), null), null),
                new BinaryTreeNode<>(3, new BinaryTreeNode<>(2), new BinaryTreeNode<>(9)));


        assertThat(pathsWithSum.countSum(root, 10)).isEqualTo(3);
        assertThat(pathsWithSum.countSum(root, 17)).isEqualTo(1);
    }
}