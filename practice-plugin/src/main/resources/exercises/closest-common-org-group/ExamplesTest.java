package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExamplesTest {
    private final Solution solution = new Solution();

    @Test
    void findsLowestCommonDepartmentForTwoSubteams() {
        String[][] links = {
                {"Company", "Eng"},
                {"Company", "HR"},
                {"Eng", "Backend"},
                {"Eng", "Frontend"}
        };
        String[][] memberships = {
                {"Backend", "Alice"},
                {"Frontend", "Bob"},
                {"HR", "Carol"}
        };
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Bob"}))
                .isEqualTo("Eng");
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Carol"}))
                .isEqualTo("Company");
    }

    @Test
    void returnsSharedLeafGroupForTeammates() {
        String[][] links = {{"Company", "Eng"}, {"Eng", "Backend"}};
        String[][] memberships = {{"Backend", "Alice"}, {"Backend", "Dave"}};
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Dave"}))
                .isEqualTo("Backend");
    }

    @Test
    void choosesDeepestGroupWhenEmployeeBelongsToMultipleGroups() {
        String[][] links = {{"Company", "Eng"}, {"Company", "HR"}, {"Eng", "Backend"}};
        String[][] memberships = {{"Backend", "Eve"}, {"HR", "Eve"}, {"HR", "Carol"}};
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Eve", "Carol"}))
                .isEqualTo("HR");
    }
}
