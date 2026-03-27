import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * You are given with a series of buildings that have windows facing west. The buildings are in a straight line, and
 * any building which is to the east of a building of equal or greater height cannot view the sunset.
 * Design an algorithm that processes buildings in east-to-west order and returns the set of buildings which
 * view the sunset.
 * Each building is specified by its height.
 */
public class SunsetView {

    public static List<Integer> canView(List<Integer> buildings) {
        Stack<Building> stack = new Stack<>();

        for (int i = 0; i < buildings.size(); i++) {
            int height = buildings.get(i);
            if (stack.isEmpty() || stack.peek().height > height) {
                stack.add(new Building(i, height));
            } else if (!stack.isEmpty() && stack.peek().height < height) {
                while (!stack.isEmpty() && stack.peek().height < height) {
                    stack.pop();
                }
                stack.add(new Building(i, height));
            }
        }

        return stack.stream()
                .map(Building::getIdx)
                .collect(Collectors.toList());
    }

    private static class Building {
        public int idx;
        public int height;

        public Building(int idx, int height) {
            this.idx = idx;
            this.height = height;
        }

        public int getIdx() {
            return idx;
        }
    }

}
