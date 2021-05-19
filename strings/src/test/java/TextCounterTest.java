import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextCounterTest {
    private TextCounter textCounter;

    @BeforeEach
    public void setUp() throws Exception {
        textCounter = new TextCounter();
    }

    @Test
    public void testNull() {
        assertThat(textCounter.count(null)).isEmpty();
    }

    @Test
    public void testBlank() {
        assertThat(textCounter.count("    ")).isEmpty();
        assertThat(textCounter.count("\n")).isEmpty();
        assertThat(textCounter.count("\n \r")).isEmpty();
    }

    @Test
    public void testCountSimple() {
        assertThat(textCounter.count("ab ab"))
                .containsExactly("2 ab");


    }

    @Test
    public void testQuickBrownFox() {
        assertThat(textCounter.count("The quick brown fox jumps over the lazy dog."))
                .contains("1 The", "1 dog", "1 fox", "1 the", "1 lazy", "1 over", "1 brown", "1 jumps", "1 quick");
    }

    @Test
    public void testParagraph() {
        assertThat(textCounter.count("How are you doing today? \n \r What are you doing!"))
                .contains("1 How", "2 are", "2 you", "1 What", "2 doing", "1 today");
    }

}