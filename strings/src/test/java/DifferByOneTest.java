import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class DifferByOneTest {
    private DifferByOne differByOne;


    @BeforeEach
    void setUp() {
        differByOne = new DifferByOne();
    }

    @Test
    void differByOne() {
        assertThat(differByOne.differByOne(new String[]{"abcd", "acbd", "aacd"}))
                .isTrue();

        assertThat(differByOne.differByOne(new String[]{"ab", "cd", "yz"}))
                .isFalse();
        assertThat(differByOne.differByOne(new String[]{"aaaddb", "aaaacd", "aaacda", "aaaaba", "aaaccd"}))
                .isTrue();
    }
}