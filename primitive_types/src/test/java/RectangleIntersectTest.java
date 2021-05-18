import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RectangleIntersectTest {

    @Test
    void intersects() {
        assertThat(RectangleIntersect.intersects(new RectangleIntersect.Rectangle(1, -1, 2, 2),
                new RectangleIntersect.Rectangle(2, 0, 2, 2)))
                .isEqualTo(new RectangleIntersect.Rectangle(2, 0, 1, 1));
    }
}