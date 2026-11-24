package com.jinloes.practice;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public final class ExampleRunner {
    private static final String USAGE =
            "Input: A-B:rate, ...; S->T, ... (comma-separated rates and queries), for example: USD-EUR:0.9, EUR-GBP:0.8; USD->GBP, GBP->USD";

    private ExampleRunner() {
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            runInput(String.join(" ", args).trim());
            return;
        }
        Solution solution = new Solution();
        double[] r1 = solution.evaluateRates(
                new String[][]{{"USD", "EUR"}, {"EUR", "GBP"}},
                new double[]{0.9, 0.8},
                new String[][]{{"USD", "GBP"}, {"GBP", "USD"}, {"USD", "USD"}, {"USD", "JPY"}});
        assertThat(r1[0]).as("Example 1 USD->GBP").isCloseTo(0.72, within(1e-5));
        assertThat(r1[1]).as("Example 1 GBP->USD").isCloseTo(1.0 / 0.72, within(1e-5));
        assertThat(r1[2]).as("Example 1 USD->USD").isCloseTo(1.0, within(1e-5));
        assertThat(r1[3]).as("Example 1 USD->JPY").isCloseTo(-1.0, within(1e-5));

        double[] r2 = solution.evaluateRates(
                new String[][]{{"USD", "CAD"}, {"EUR", "GBP"}},
                new double[]{1.35, 0.8},
                new String[][]{{"USD", "GBP"}});
        assertThat(r2).as("Example 2").containsExactly(-1.0);

        double[] r3 = solution.evaluateRates(
                new String[][]{{"USD", "EUR"}},
                new double[]{0.9},
                new String[][]{{"BTC", "BTC"}});
        assertThat(r3).as("Example 3").containsExactly(-1.0);
        System.out.println("Examples passed: 3");
    }

    private static void runInput(String input) {
        String[] sections = input.split(";", -1);
        if (sections.length != 2) {
            System.out.println("Provide 'rates; queries'. " + USAGE);
            return;
        }
        try {
            String[] rawPairs = sections[0].trim().isEmpty() ? new String[0] : sections[0].split(",");
            String[][] pairs = new String[rawPairs.length][2];
            double[] rates = new double[rawPairs.length];
            for (int i = 0; i < rawPairs.length; i++) {
                String token = rawPairs[i].trim();
                int colon = token.indexOf(':');
                int dash = token.indexOf('-');
                if (dash <= 0 || colon <= dash + 1 || colon == token.length() - 1) {
                    System.out.println("Invalid rate '" + token + "'. " + USAGE);
                    return;
                }
                pairs[i][0] = token.substring(0, dash).trim();
                pairs[i][1] = token.substring(dash + 1, colon).trim();
                rates[i] = Double.parseDouble(token.substring(colon + 1).trim());
            }
            String[] rawQueries = sections[1].trim().isEmpty() ? new String[0] : sections[1].split(",");
            String[][] queries = new String[rawQueries.length][2];
            for (int i = 0; i < rawQueries.length; i++) {
                String[] parts = rawQueries[i].trim().split("->", -1);
                if (parts.length != 2) {
                    System.out.println("Invalid query '" + rawQueries[i].trim() + "'. " + USAGE);
                    return;
                }
                queries[i][0] = parts[0].trim();
                queries[i][1] = parts[1].trim();
            }
            double[] results = new Solution().evaluateRates(pairs, rates, queries);
            StringBuilder out = new StringBuilder();
            for (int i = 0; i < results.length; i++) {
                if (i > 0) {
                    out.append(", ");
                }
                out.append(String.format(Locale.ROOT, "%.5f", results[i]));
            }
            System.out.println(out);
        } catch (NumberFormatException exception) {
            System.out.println("Invalid rate number. " + USAGE);
        }
    }
}
