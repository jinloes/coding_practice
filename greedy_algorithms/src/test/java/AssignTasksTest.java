import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssignTasksTest {

    @Test
    void optimumTaskAssignment() {
        assertThat(AssignTasks.optimumTaskAssignment(new ArrayList<>(List.of(5, 2, 1, 6, 4, 4))))
                .containsExactlyInAnyOrder(
                        List.of(1, 6),
                        List.of(2, 5),
                        List.of(4, 4)
                );
    }
}