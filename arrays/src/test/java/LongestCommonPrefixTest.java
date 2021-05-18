import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class LongestCommonPrefixTest {
    @Test
    public void testCompute() {
        assertThat(LongestCommonPrefix.compute(new String[]{"geeksforgeeks", "geeks", "geek", "geezer"}))
                .isEqualTo("gee");
        assertThat(LongestCommonPrefix.compute(new String[]{"apple", "ape", "april"}))
                .isEqualTo("ap");
    }
}
