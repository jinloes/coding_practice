package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void returnsThreeLargestValues(){ assertThat(Solution.kLargest(tree(5,3,8,2,4,7,9),3)).containsExactly(9,8,7); }
    @Test
    void returnsAllValuesWhenKIsTooLarge(){ assertThat(Solution.kLargest(tree(4),5)).containsExactly(4); }
    @Test
    void emptyTreeHasNoLargestValues(){ assertThat(Solution.kLargest(null,2)).isEmpty(); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
