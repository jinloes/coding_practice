import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCharsTest {

    @Test
    public void delete() {
        assertThat(DeleteChars.delete("Battle of the Vowels: Hawaii vs. Grozny".toCharArray(), "aeiou"))
                .isEqualTo("Bttl f th Vwls: Hw vs. Grzny");
    }

    @Test
    public void deleteWithoutExtraMemory() {
        assertThat(DeleteChars.deleteWithoutExtraMemory("Battle of the Vowels: Hawaii vs. Grozny".toCharArray(), "aeiou"))
                .isEqualTo("Bttl f th Vwls: Hw vs. Grzny");
    }
}