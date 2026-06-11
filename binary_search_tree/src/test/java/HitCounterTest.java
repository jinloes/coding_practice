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

    @Test
    void hitsOutsideWindowAreNotCounted() {
        hitCounter.hit(1);
        hitCounter.hit(2);
        hitCounter.hit(3);

        // 301 is 300 seconds after timestamp 1, so the hit at 1 falls outside the window.
        assertThat(hitCounter.getHits(301)).isEqualTo(2);
    }
}