/**
 * Write a program that computes the maximum profit that can be made by buying and selling a share at most twice.
 * The second buy must be made on another date after the first sale.
 */
public class BuyAndSellStock2 {

    public static int maxProfit(int[] prices) {
        if (prices == null) {
            return 0;
        }

        int maxProfit = 0;
        int[] firstProfits = new int[prices.length];

        int minPrice = Integer.MAX_VALUE;

        for (int i = 0; i < prices.length; i++) {
            minPrice = Math.min(minPrice, prices[i]);
            maxProfit = Math.max(maxProfit, prices[i] - minPrice);
            firstProfits[i] = maxProfit;
        }

        int maxPrice = 0;

        for (int i = prices.length - 1; i > 0; i--) {
            maxPrice = Math.max(maxPrice, prices[i]);
            maxProfit = Math.max(maxProfit, maxPrice - prices[i] + firstProfits[i - 1]);
        }

        return maxProfit;
    }
}
