import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class TelephoneWordsTest {

    @Test
    public void generate() {
        assertThat(TelephoneWords.generate("8662665")).contains("TOOCOOL");
    }
}