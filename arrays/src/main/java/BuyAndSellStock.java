/**
 * Write a program that takes an array denoting the daily stock price, and returns the maximum profit that could be
 * made by buying and then selling one share of that stock.
 */
public class BuyAndSellStock {


    public static int maxProfit(int[] arr) {
        if (arr == null) {
            return 0;
        }
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : arr) {
            maxProfit = Math.max(maxProfit, price - minPrice);
            minPrice = Math.min(minPrice, price);
        }

        return maxProfit;
    }
}
