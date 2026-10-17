package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyTreeContributesZero(){ assertThat(Solution.sumRootToLeaf(null)).isZero(); }
    @Test
    void zeroBitsAreAllowed(){ assertThat(Solution.sumRootToLeaf(new Solution.TreeNode(0,new Solution.TreeNode(0),new Solution.TreeNode(1)))).isEqualTo(1); }
    @Test
    void deeperMixedTree(){ Solution.TreeNode root=new Solution.TreeNode(1,new Solution.TreeNode(1,new Solution.TreeNode(1),null),new Solution.TreeNode(0,new Solution.TreeNode(1),new Solution.TreeNode(0))); assertThat(Solution.sumRootToLeaf(root)).isEqualTo(16); }
}



