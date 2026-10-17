package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyTreeHasNone(){ assertThat(Solution.countRootPrefixes(null,0)).isZero(); }
    @Test
    void countsZeroAndNegativePrefixes(){ Solution.TreeNode root=new Solution.TreeNode(0,new Solution.TreeNode(-1,new Solution.TreeNode(1),null),new Solution.TreeNode(0,new Solution.TreeNode(0),null)); assertThat(Solution.countRootPrefixes(root,0)).isEqualTo(4); }
    @Test
    void doesNotRestartBelowRoot(){ Solution.TreeNode root=new Solution.TreeNode(4,new Solution.TreeNode(5),new Solution.TreeNode(2)); assertThat(Solution.countRootPrefixes(root,5)).isZero(); }
}



