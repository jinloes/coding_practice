import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class TaskSchedulerTest {
    private TaskScheduler taskScheduler;

    public static Stream<Arguments> tasks() {
        return Stream.of(
                Arguments.of(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 2, 8),
                Arguments.of(new char[]{'A', 'A', 'A', 'B', 'B', 'B'}, 0, 6),
                Arguments.of(new char[]{'A', 'A', 'A', 'A', 'A', 'A', 'B', 'C', 'D', 'E', 'F', 'G'}, 2, 16)
        );
    }

    @BeforeEach
    void setUp() {
        taskScheduler = new TaskScheduler();
    }

    @ParameterizedTest
    @MethodSource("tasks")
    void leastInterval(char[] tasks, int count, int expected) {
        assertThat(taskScheduler.leastInterval(tasks, count))
                .isEqualTo(expected);
    }
}