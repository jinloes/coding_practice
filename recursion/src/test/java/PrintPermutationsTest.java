import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PrintPermutationsTest {

    @Test
    public void generate() {
        assertThat(PrintPermutations.generate("hat"))
                .containsExactlyInAnyOrder("tha", "aht", "tah", "ath", "hta", "hat");
    }
}