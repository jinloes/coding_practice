import java.util.ArrayList;
import java.util.List;

/**
 * Question:
 * <p>
 * You are a professional painter planning to paint a bunch of houses along a street. Each house is a slightly
 * different size and will therefore pay you a different amount of money.  The only constraint stopping you from
 * painting all the houses is that the residents don't want to look like they are copying their neighbors.
 * Therefore, someone will refuse to let you paint their house if you already painted both of their neighbors' houses.
 * <p>
 * Given an array of money the houses will pay, what's the most you can earn on this street?
 * <p>
 * Examples:
 * [100, 50] => [100, 50] => $150
 * [100, 70, 60] => [100, 70, 60] => $170
 * [60, 70, 100] => [60, 70, 100] => $170
 * [40, 100, 50, 20, 100] => [40, 100, 50, 20, 100] => $260
 */
public class Painting {
    public static int maxProfit(int[] houses) {
        if (houses == null || houses.length == 0) {
            return 0;
        }
        return maxProfit(houses, 0, new ArrayList<>());
    }

    private static int maxProfit(int[] houses, int index, List<Integer> indicies) {
        if (index >= houses.length) {
            return 0;
        }

        int max1 = maxProfit(houses, index + 1, new ArrayList<>());
        int max2 = 0;
        if (indicies.size() < 2 || indicies.get(indicies.size() - 1) - indicies.get(indicies.size() - 2) > 1) {
            List<Integer> newIndicies = new ArrayList<>(indicies);
            newIndicies.add(index);
            max2 = maxProfit(houses, index + 1, newIndicies) + houses[index];
        }

        return Math.max(max1, max2);

    }
}
