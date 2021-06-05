import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HitCounterTest {
    private HitCounter hitCounter;

    @BeforeEach
    void setUp() {
        hitCounter = new HitCounter();
    }

    @Test
    void getHits() {
        hitCounter.hit(1);
        hitCounter.hit(1);
        hitCounter.hit(1);
        hitCounter.hit(300);

        assertThat(hitCounter.getHits(300)).isEqualTo(4);
    }
}