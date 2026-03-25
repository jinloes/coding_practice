package com.jinloes.coding_practice.arrays;

/**
 * Computes the maximum profit that can be made by buying and selling a share at most twice.
 * The second buy must be made on another date after the first sale.
 */
public class BuyAndSellStock2 {

    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }

        int maxProfit = 0;
        int[] firstProfits = new int[prices.length];

        int minPrice = Integer.MAX_VALUE;

        // Forward pass: Calculate maximum profit for one transaction up to each day
        // This tracks the best profit achievable by buying and selling once from day 0 to day i
        for (int i = 0; i < prices.length; i++) {
            // Update the minimum price seen so far
            minPrice = Math.min(minPrice, prices[i]);
            // Calculate profit if we sell at current price
            maxProfit = Math.max(maxProfit, prices[i] - minPrice);
            // Store the best profit achievable up to day i
            firstProfits[i] = maxProfit;
        }

        int maxPrice = Integer.MIN_VALUE;

        // Backward pass: Calculate maximum profit for second transaction after each day
        // We combine the profit from the first transaction (up to day i-1) with
        // the profit from buying on day i and selling at the highest price after day i
        for (int i = prices.length - 1; i >= 1; i--) {
            // Update the maximum price seen from the end up to day i
            maxPrice = Math.max(maxPrice, prices[i]);
            // Calculate profit if we buy at current price (i) and sell at maxPrice
            // Total profit = profit from first transaction (up to day i-1) + profit from second transaction
            // If second transaction is unprofitable, firstProfits[i-1] will be chosen
            maxProfit = Math.max(maxProfit, maxPrice - prices[i] + firstProfits[i - 1]);
        }

        return maxProfit;
    }
}
