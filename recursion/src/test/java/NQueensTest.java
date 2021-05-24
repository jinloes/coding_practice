import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class NQueensTest {

    @Test
    void solve() {
        assertThat(NQueens.solve(4))
                .contains(
                        List.of(1, 3, 0, 2),
                        List.of(2, 0, 3, 1)
                );
    }
}