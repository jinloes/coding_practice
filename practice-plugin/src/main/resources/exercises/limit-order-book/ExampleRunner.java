package com.jinloes.practice;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: comma-separated orders 'B|S id price qty', for example: S 1 100 10, S 2 102 5, B 3 102 12";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution b1 = new Solution();
        assertThat(b1.submitOrder(1, false, 100, 10)).as("Example 1 order 1").isEmpty();
        assertThat(b1.submitOrder(2, false, 102, 5)).as("Example 1 order 2").isEmpty();
        assertThat(b1.submitOrder(3, true, 102, 12)).as("Example 1 order 3").containsExactly(
                new Solution.Trade(3, 1, 100, 10),
                new Solution.Trade(3, 2, 102, 2));

        Solution b2 = new Solution();
        assertThat(b2.submitOrder(1, true, 100, 5)).as("Example 2 order 1").isEmpty();
        assertThat(b2.submitOrder(2, true, 100, 5)).as("Example 2 order 2").isEmpty();
        assertThat(b2.submitOrder(3, false, 99, 7)).as("Example 2 order 3").containsExactly(
                new Solution.Trade(1, 3, 100, 5),
                new Solution.Trade(2, 3, 100, 2));

        Solution b3 = new Solution();
        assertThat(b3.submitOrder(1, true, 95, 4)).as("Example 3 order 1").isEmpty();
        assertThat(b3.submitOrder(2, false, 105, 4)).as("Example 3 order 2").isEmpty();
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        Solution book = new Solution();
        StringBuilder out = new StringBuilder();
        try {
            String[] orders = input.split(",");
            for (int i = 0; i < orders.length; i++) {
                String[] parts = orders[i].trim().split("\\s+");
                if (parts.length != 4 || (!"B".equalsIgnoreCase(parts[0]) && !"S".equalsIgnoreCase(parts[0]))) {
                    System.out.println("Invalid order '" + orders[i].trim() + "'. " + USAGE);
                    return;
                }
                boolean isBuy = "B".equalsIgnoreCase(parts[0]);
                int id = Integer.parseInt(parts[1]);
                int price = Integer.parseInt(parts[2]);
                int qty = Integer.parseInt(parts[3]);
                List<Solution.Trade> trades = book.submitOrder(id, isBuy, price, qty);
                if (i > 0) {
                    out.append(", ");
                }
                out.append(trades.stream()
                        .map(t -> t.buyId() + "x" + t.sellId() + "@" + t.price() + "(" + t.quantity() + ")")
                        .collect(Collectors.joining(", ", "[", "]")));
            }
            System.out.println(out);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid integer in order. " + USAGE);
        }
    }
}
