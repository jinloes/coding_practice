package com.jinloes.practice;


import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: set cat meow 5, get cat 5, get cat 4, get cat 9";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        example1();
        example2();
        example3();
        System.out.println("Examples passed: 3");
    }

    private static void example1() {
        Solution store = new Solution();
        store.set("cat", "meow", 5);
        assertThat(store.get("cat", 5)).as("get(cat, 5)").isEqualTo("meow");
        assertThat(store.get("cat", 4)).as("get(cat, 4)").isEmpty();
        assertThat(store.get("cat", 9)).as("get(cat, 9)").isEqualTo("meow");
    }

    private static void example2() {
        Solution store = new Solution();
        store.set("a", "x", 1);
        store.set("a", "y", 4);
        assertThat(store.get("a", 3)).as("get(a, 3)").isEqualTo("x");
        assertThat(store.get("a", 4)).as("get(a, 4)").isEqualTo("y");
        assertThat(store.get("a", 1)).as("get(a, 1)").isEqualTo("x");
    }

    private static void example3() {
        Solution store = new Solution();
        assertThat(store.get("ghost", 1)).as("get(ghost, 1)").isEmpty();
    }



    private static void runInput(String input) {
        Solution store = new Solution();
        for (String raw : input.split(",")) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            String[] parts = operation.split("\\s+");
            try {
                if (parts[0].equals("set") && parts.length == 4) {
                    int time = Integer.parseInt(parts[3]);
                    store.set(parts[1], parts[2], time);
                    System.out.println("set(" + parts[1] + ", " + parts[2] + ", " + time + ")");
                } else if (parts[0].equals("get") && parts.length == 3) {
                    int time = Integer.parseInt(parts[2]);
                    System.out.println("get(" + parts[1] + ", " + time + ") = \"" + store.get(parts[1], time) + "\"");
                } else {
                    System.out.println("Could not read '" + operation + "'. " + USAGE);
                    return;
                }
            } catch (NumberFormatException exception) {
                System.out.println("'" + operation + "' needs a whole-number time. " + USAGE);
                return;
            }
        }
    }
}
