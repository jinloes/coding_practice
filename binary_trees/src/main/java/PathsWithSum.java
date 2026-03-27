

/**
 * Given a binary tree, count the number of paths that sum to a given value.
 */
public class PathsWithSum {

    public int countSum(BinaryTreeNode<Integer> root, int sum) {
        if (root == null) {
            return 0;
        }
        return countSumInternal(root, sum, 0);
    }

    private int countSumInternal(BinaryTreeNode<Integer> root, int sum, int runningSum) {
        if (root == null) {
            return 0;
        }

        int totalPaths = 0;
        if (runningSum + root.data == sum) {
            totalPaths++;
        }

        return totalPaths + countSumInternal(root.left, sum, runningSum + root.data)
                + countSumInternal(root.right, sum, runningSum + root.data);

    }
}
