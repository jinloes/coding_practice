import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SalaryThresholdTest {

    @Test
    void findSalaryCap() {
        assertThat(SalaryThreshold.findSalaryCap(210, Lists.newArrayList(20.0, 30.0, 40.0, 90.0, 100.0)))
                .isEqualTo(60);

        assertThat(SalaryThreshold.findSalaryCap(510, Lists.newArrayList(20.0, 30.0, 40.0, 90.0, 100.0)))
                .isEqualTo(-1.0);
    }
}