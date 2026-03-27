import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinimizeWaitTimeTest {

    @Test
    void minimumTotalWaitingTime() {
        assertThat(MinimizeWaitTime.minimumTotalWaitingTime(Lists.newArrayList(2, 5, 1, 3)))
                .isEqualTo(10);
    }
}