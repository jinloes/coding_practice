package binary_search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GuessGameTest {

    @Test
    void guess() {
        GuessGame.GuessGameImpl guessGame = new GuessGame.GuessGameImpl(3);

        assertThat(guessGame.guessNumber(5)).isEqualTo(3);
    }
}