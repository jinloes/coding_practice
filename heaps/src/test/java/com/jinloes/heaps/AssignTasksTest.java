package com.jinloes.heaps;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AssignTasksTest {
    private AssignTasks assignTasks;

    @BeforeEach
    void setUp() {
        assignTasks = new AssignTasks();
    }

    @Test
    void assignTasks() {
        assertThat(assignTasks.assignTasks(new int[]{3, 3, 2}, new int[]{1, 2, 3, 2, 1, 2}))
                .containsExactly(2, 2, 0, 2, 1, 2);
    }
}