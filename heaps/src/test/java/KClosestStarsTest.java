import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class KClosestStarsTest {

    @Test
    void get() {
        KClosestStars.Star star1 = new KClosestStars.Star(1, 1, 1);
        KClosestStars.Star star2 = new KClosestStars.Star(2, 2, 2);
        KClosestStars.Star star3 = new KClosestStars.Star(2, 1, .5);
        KClosestStars.Star star4 = new KClosestStars.Star(.5, 1, .5);

        assertThat(KClosestStars.get(2, Lists.newArrayList(star1, star2, star3, star4)))
                .containsExactly(star4, star1);
    }
}