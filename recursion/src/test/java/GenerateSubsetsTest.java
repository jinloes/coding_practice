import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GenerateSubsetsTest {

    @Test
    void generate() {

        assertThat(GenerateSubsets.generate(5, 2))
                .containsExactlyInAnyOrder(
                        List.of(1, 2),
                        List.of(1, 3),
                        List.of(1, 4),
                        List.of(1, 5),
                        List.of(2, 3),
                        List.of(2, 4),
                        List.of(2, 5),
                        List.of(3, 4),
                        List.of(3, 5),
                        List.of(4, 5)
                );
    }
}