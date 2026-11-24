package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    private final Solution solution = new Solution();

    @Test
    void returnsNullWhenAnyTargetEmployeeIsUnknown() {
        String[][] links = {{"Company", "Eng"}};
        String[][] memberships = {{"Eng", "Alice"}};
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Ghost"}))
                .isNull();
    }

    @Test
    void handlesSingleGroupWithNoLinks() {
        String[][] links = {};
        String[][] memberships = {{"Core", "Alice"}, {"Core", "Bob"}};
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Bob"}))
                .isEqualTo("Core");
    }

    @Test
    void handlesEmployeeAtParentGroupAndAnotherInChildGroup() {
        String[][] links = {{"Company", "Eng"}, {"Eng", "Backend"}, {"Backend", "Storage"}};
        String[][] memberships = {{"Eng", "VP"}, {"Storage", "Dev"}};
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"VP", "Dev"}))
                .isEqualTo("Eng");
    }

    @Test
    void breaksEqualDepthTiesLexicographically() {
        String[][] links = {{"Root", "Beta"}, {"Root", "Alpha"}};
        String[][] memberships = {
                {"Beta", "Alice"}, {"Beta", "Bob"},
                {"Alpha", "Alice"}, {"Alpha", "Bob"}
        };
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"Alice", "Bob"}))
                .isEqualTo("Alpha");
    }

    @Test
    void handlesDuplicateTargetsAndThreeEmployees() {
        String[][] links = {
                {"Company", "Eng"},
                {"Eng", "Platform"},
                {"Platform", "Cloud"},
                {"Platform", "Data"},
                {"Eng", "Mobile"}
        };
        String[][] memberships = {
                {"Cloud", "A"},
                {"Data", "B"},
                {"Platform", "C"}
        };
        assertThat(solution.closestCommonGroup(links, memberships, new String[]{"A", "B", "C", "A"}))
                .isEqualTo("Platform");
    }
}
