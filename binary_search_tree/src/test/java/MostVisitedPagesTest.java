import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MostVisitedPagesTest {

    @Test
    void get() {
        assertThat(MostVisitedPages.get(List.of("g", "a", "t", "t", "a", "a", "a", "g", "t", "c"), 2))
                .containsExactly("a", "t");
    }
}