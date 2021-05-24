import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PalindromicDecompositionsTest {

    @Test
    void decompose() {
        assertThat(PalindromicDecompositions.decompose("0204451881"))
                .isEqualTo(List.of(
                        List.of("0", "2", "0", "4", "4", "5", "1", "8", "8", "1"),
                        List.of("0", "2", "0", "4", "4", "5", "1", "88", "1"),
                        List.of("0", "2", "0", "4", "4", "5", "1881"),
                        List.of("0", "2", "0", "44", "5", "1", "8", "8", "1"),
                        List.of("0", "2", "0", "44", "5", "1", "88", "1"),
                        List.of("0", "2", "0", "44", "5", "1881"),
                        List.of("020", "4", "4", "5", "1", "8", "8", "1"),
                        List.of("020", "4", "4", "5", "1", "88", "1"),
                        List.of("020", "4", "4", "5", "1881"),
                        List.of("020", "44", "5", "1", "8", "8", "1"),
                        List.of("020", "44", "5", "1", "88", "1"),
                        List.of("020", "44", "5", "1881")
                ));
    }
}