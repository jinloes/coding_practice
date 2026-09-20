package com.jinloes.practice;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: operations separated by commas, for example: add 7, add 2, removeMin, peek, size";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution heap = new Solution();
        heap.add(7);
        heap.add(2);
        heap.add(5);
        assertThat(heap.peek()).as("Example 1: peek returns the smallest value").isEqualTo(2);
        assertThat(heap.removeMin()).as("Example 1: first removeMin").isEqualTo(2);
        assertThat(heap.removeMin()).as("Example 1: second removeMin").isEqualTo(5);
        assertThat(heap.removeMin()).as("Example 1: third removeMin").isEqualTo(7);

        Solution duplicates = new Solution();
        duplicates.add(4);
        duplicates.add(1);
        duplicates.add(1);
        assertThat(duplicates.removeMin()).as("Example 2: first duplicate minimum").isEqualTo(1);
        assertThat(duplicates.removeMin()).as("Example 2: second duplicate minimum").isEqualTo(1);

        Solution empty = new Solution();
        assertThat(empty.isEmpty()).as("Example 3: a new heap must be empty").isTrue();
        assertThatThrownBy(empty::removeMin).isInstanceOf(NoSuchElementException.class);
        assertThatThrownBy(empty::peek).isInstanceOf(NoSuchElementException.class);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] operations = input.split(",");
        Solution heap = new Solution();
        for (String raw : operations) {
            String operation = raw.trim();
            if (operation.isEmpty()) {
                continue;
            }
            if (!apply(heap, operation)) {
                return;
            }
        }
    }

    private static boolean apply(Solution heap, String operation) {
        String[] parts = operation.split("\\s+");
        String name = parts[0];
        try {
            switch (name) {
                case "add" -> {
                    if (parts.length != 2) {
                        System.out.println("'" + operation + "' needs exactly one value. " + USAGE);
                        return false;
                    }
                    int value = Integer.parseInt(parts[1]);
                    heap.add(value);
                    System.out.println("add(" + value + ")");
                }
                case "removeMin" -> System.out.println("removeMin() = " + heap.removeMin());
                case "peek" -> System.out.println("peek() = " + heap.peek());
                case "size" -> System.out.println("size() = " + heap.size());
                case "isEmpty" -> System.out.println("isEmpty() = " + heap.isEmpty());
                default -> {
                    System.out.println("Unknown operation '" + operation + "'. " + USAGE);
                    return false;
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("'" + operation + "' does not name an integer. " + USAGE);
            return false;
        } catch (NoSuchElementException exception) {
            System.out.println(name + "() threw NoSuchElementException because the heap is empty");
        }
        return true;
    }
}
