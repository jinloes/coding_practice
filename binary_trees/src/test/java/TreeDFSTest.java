

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeDFSTest extends BaseTreeTest {

    @Test
    public void search() {
        assertThat(TreeDFS.search(root, 5)).isTrue();
        assertThat(TreeDFS.search(root, 2)).isTrue();
        assertThat(TreeDFS.search(root, 21232)).isFalse();
        assertThat(TreeDFS.search(root, null)).isFalse();
    }
}