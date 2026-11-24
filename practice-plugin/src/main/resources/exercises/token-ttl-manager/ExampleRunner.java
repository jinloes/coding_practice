package com.jinloes.practice;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: ttl; comma-separated ops 'G id t', 'R id t', 'C t', for example: 5; G a 2, C 6, G b 7, R b 10, C 15";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution m1 = new Solution(5);
        m1.renew("a", 1);
        m1.generate("a", 2);
        assertThat(m1.countUnexpired(6)).as("Example 1 count 1").isEqualTo(1);
        m1.generate("b", 7);
        m1.renew("a", 8);
        m1.renew("b", 10);
        assertThat(m1.countUnexpired(15)).as("Example 1 count 2").isEqualTo(0);

        Solution m2 = new Solution(5);
        m2.generate("x", 1);
        m2.renew("x", 6);
        assertThat(m2.countUnexpired(6)).as("Example 2 count").isEqualTo(0);

        Solution m3 = new Solution(10);
        m3.generate("a", 1);
        m3.generate("b", 5);
        assertThat(m3.countUnexpired(10)).as("Example 3 count 1").isEqualTo(2);
        assertThat(m3.countUnexpired(11)).as("Example 3 count 2").isEqualTo(1);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'ttl; ops'. " + USAGE);
            return;
        }
        try {
            int ttl = Integer.parseInt(sections[0].trim());
            Solution manager = new Solution(ttl);
            List<String> outputs = new ArrayList<>();
            for (String raw : sections[1].split(",")) {
                String[] parts = raw.trim().split("\\s+");
                if (parts.length == 3 && "G".equalsIgnoreCase(parts[0])) {
                    manager.generate(parts[1], Integer.parseInt(parts[2]));
                } else if (parts.length == 3 && "R".equalsIgnoreCase(parts[0])) {
                    manager.renew(parts[1], Integer.parseInt(parts[2]));
                } else if (parts.length == 2 && "C".equalsIgnoreCase(parts[0])) {
                    outputs.add(String.valueOf(manager.countUnexpired(Integer.parseInt(parts[1]))));
                } else {
                    System.out.println("Invalid op '" + raw.trim() + "'. " + USAGE);
                    return;
                }
            }
            System.out.println(String.join(", ", outputs));
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer in input. " + USAGE);
        }
    }
}
