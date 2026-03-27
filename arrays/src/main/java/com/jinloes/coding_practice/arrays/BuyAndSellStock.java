package com.jinloes.coding_practice.arrays;

/**
 * Computes the maximum profit that can be made by buying and selling a share exactly once.
 * This is the classic "Best Time to Buy and Sell Stock" problem.
 * <p>
 * The algorithm tracks the minimum price seen so far and calculates the potential
 * profit if we sell at the current price. The maximum of all these potential profits
 * is the answer.
 */
public class BuyAndSellStock {

    /**
     * Calculates the maximum profit achievable by buying and selling a stock once.
     *
     * @param prices array of daily stock prices
     * @return maximum profit, or 0 if no profit can be made
     */
    public static int maxProfit(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }

        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        // Iterate through each price to find the best buying and selling points
        for (int price : prices) {
            // Calculate potential profit if we sell at current price
            // This checks if selling today gives us better profit than we've seen so far
            maxProfit = (int) Math.max(maxProfit, (long) price - minPrice);

            // Update the minimum price seen so far
            // This represents the best day to buy up to the current day
            minPrice = Math.min(minPrice, price);
        }

        return maxProfit;
    }
}
