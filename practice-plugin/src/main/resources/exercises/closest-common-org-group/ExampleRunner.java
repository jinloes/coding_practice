package com.jinloes.practice;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: parent>child, ...; group:emp, ...; target1, target2, for example: Company>Eng, Company>HR, Eng>Backend, Eng>Frontend; Backend:Alice, Frontend:Bob; Alice, Bob";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        assertThat(solution.closestCommonGroup(
                new String[][]{{"Company", "Eng"}, {"Company", "HR"}, {"Eng", "Backend"}, {"Eng", "Frontend"}},
                new String[][]{{"Backend", "Alice"}, {"Frontend", "Bob"}, {"HR", "Carol"}},
                new String[]{"Alice", "Bob"}))
                .as("Example 1").isEqualTo("Eng");

        assertThat(solution.closestCommonGroup(
                new String[][]{{"Company", "Eng"}, {"Eng", "Backend"}},
                new String[][]{{"Backend", "Alice"}, {"Backend", "Dave"}},
                new String[]{"Alice", "Dave"}))
                .as("Example 2").isEqualTo("Backend");

        assertThat(solution.closestCommonGroup(
                new String[][]{{"Company", "Eng"}, {"Company", "HR"}, {"Eng", "Backend"}},
                new String[][]{{"Backend", "Eve"}, {"HR", "Eve"}, {"HR", "Carol"}},
                new String[]{"Eve", "Carol"}))
                .as("Example 3").isEqualTo("HR");
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 3) {
            System.out.println("Provide 'links; memberships; targets'. " + USAGE);
            return;
        }
        String[] rawLinks = sections[0].trim().isEmpty() ? new String[0] : sections[0].split(",");
        String[][] links = new String[rawLinks.length][2];
        for (int i = 0; i < rawLinks.length; i++) {
            String[] parts = rawLinks[i].trim().split(">", -1);
            if (parts.length != 2) {
                System.out.println("Invalid link '" + rawLinks[i].trim() + "'. " + USAGE);
                return;
            }
            links[i][0] = parts[0].trim();
            links[i][1] = parts[1].trim();
        }
        String[] rawMem = sections[1].trim().isEmpty() ? new String[0] : sections[1].split(",");
        String[][] memberships = new String[rawMem.length][2];
        for (int i = 0; i < rawMem.length; i++) {
            String[] parts = rawMem[i].trim().split(":", -1);
            if (parts.length != 2) {
                System.out.println("Invalid membership '" + rawMem[i].trim() + "'. " + USAGE);
                return;
            }
            memberships[i][0] = parts[0].trim();
            memberships[i][1] = parts[1].trim();
        }
        String[] rawTargets = sections[2].trim().isEmpty() ? new String[0] : sections[2].split(",");
        String[] targets = new String[rawTargets.length];
        for (int i = 0; i < rawTargets.length; i++) {
            targets[i] = rawTargets[i].trim();
        }
        System.out.println(new Solution().closestCommonGroup(links, memberships, targets));
    }
}
