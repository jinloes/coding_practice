

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeBFSTest extends BaseTreeTest {

    @Test
    public void testSearch() {
        assertThat(TreeBFS.search(root, 5)).isTrue();
        assertThat(TreeBFS.search(root, 1)).isTrue();
        assertThat(TreeBFS.search(root, 999)).isFalse();
        assertThat(TreeBFS.search(root, null)).isFalse();
    }

}