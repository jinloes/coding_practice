import com.google.common.collect.Lists;
import io.vavr.Tuple;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssignTasksTest {

    @Test
    void optimumTaskAssignment() {
        assertThat(AssignTasks.optimumTaskAssignment(Lists.newArrayList(5, 2, 1, 6, 4, 4)))
                .containsExactlyInAnyOrder(
                        Tuple.of(1, 6),
                        Tuple.of(2, 5),
                        Tuple.of(4, 4)
                );
    }
}