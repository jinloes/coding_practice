import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchTest {

    @Test
    public void search() {
        assertThat(BinarySearch.search(new int[]{1, 2, 3, 4, 5, 6}, 3)).isEqualTo(2);
        assertThat(BinarySearch.search(new int[]{1, 2, 3, 4, 5, 6}, 100)).isEqualTo(-1);
        assertThat(BinarySearch.search(new int[]{1, 2, 3, 4, 5, 6}, 1)).isEqualTo(0);
        assertThat(BinarySearch.search(new int[]{1, 2, 3, 4, 5, 6}, 6)).isEqualTo(5);
    }
}