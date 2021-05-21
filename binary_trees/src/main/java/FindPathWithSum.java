/**
 * Write a program which takes as input an integer and a binary tree with integer node weights, and checks if there
 * exists a leaf whose path weight equals the given integer.
 */
public class FindPathWithSum {
    public static boolean exists(BinaryTreeNode<Integer> root, int sum) {
        return exists(root, sum, 0);
    }

    private static boolean exists(BinaryTreeNode<Integer> root, int sum, int runningSum) {
        if (root == null) {
            return sum == runningSum;
        }

        runningSum += root.data;

        return exists(root.left, sum, runningSum) || exists(root.right, sum, runningSum);
    }
}
