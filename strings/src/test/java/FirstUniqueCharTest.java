import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FirstUniqueCharTest {

    @Test
    public void firstUniqChar() {
        assertThat(FirstUniqueChar.firstUniqChar("leetcode")).isEqualTo(0);

        assertThat(FirstUniqueChar.firstUniqChar("z")).isEqualTo(0);
    }
}