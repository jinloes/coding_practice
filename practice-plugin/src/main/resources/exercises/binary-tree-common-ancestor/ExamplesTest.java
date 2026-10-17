package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void rootCanBeCommonAncestor(){ Solution.TreeNode root=tree(3,5,1,6,2,0,8); assertThat(Solution.lowestCommonAncestor(root,root.left,root.right)).isSameAs(root); }
    @Test
    void oneNodeCanBeAncestorOfTheOther(){ Solution.TreeNode root=tree(3,5,1,6,2,0,8); assertThat(Solution.lowestCommonAncestor(root,root.left,root.left.right)).isSameAs(root.left); }
    @Test
    void emptyTreeHasNoAncestor(){ assertThat(Solution.lowestCommonAncestor(null,new Solution.TreeNode(1),new Solution.TreeNode(2))).isNull(); }
    private static Solution.TreeNode tree(Integer... levels) {
        if (levels.length == 0 || levels[0] == null) return null;
        Solution.TreeNode root = new Solution.TreeNode(levels[0]); java.util.ArrayDeque<Solution.TreeNode> q = new java.util.ArrayDeque<>(); q.add(root); int i = 1;
        while (!q.isEmpty() && i < levels.length) { Solution.TreeNode n = q.poll(); if (i < levels.length && levels[i] != null) { n.left = new Solution.TreeNode(levels[i]); q.add(n.left); } i++; if (i < levels.length && levels[i] != null) { n.right = new Solution.TreeNode(levels[i]); q.add(n.right); } i++; }
        return root;
    }
}
