import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FirstNonRepeatedCharTest {

    @Test
    public void find() {
        assertThat(FirstNonRepeatedChar.find("total")).isEqualTo('o');
        assertThat(FirstNonRepeatedChar.find("teeter")).isEqualTo('r');
    }
}