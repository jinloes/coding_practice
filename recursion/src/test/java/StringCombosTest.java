import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class StringCombosTest {

    @Test
    public void generate() {
        assertThat(StringCombos.generate("123")).containsExactlyInAnyOrder("1", "2", "3", "12", "13", "23", "123");
    }
}