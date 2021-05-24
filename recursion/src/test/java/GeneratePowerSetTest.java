import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratePowerSetTest {

    @Test
    void generate() {
        assertThat(GeneratePowerSet.generate(Lists.newArrayList(0, 1, 2)))
                .containsExactlyInAnyOrder(
                        List.of(),
                        List.of(0),
                        List.of(1),
                        List.of(2),
                        List.of(0, 1),
                        List.of(1, 2),
                        List.of(0, 2),
                        List.of(0, 1, 2)
                );
    }
}