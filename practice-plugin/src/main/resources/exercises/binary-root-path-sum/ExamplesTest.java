package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void addsThreeBinaryPaths(){ Solution.TreeNode root=new Solution.TreeNode(1,new Solution.TreeNode(0,new Solution.TreeNode(0),new Solution.TreeNode(1)),new Solution.TreeNode(1)); assertThat(Solution.sumRootToLeaf(root)).isEqualTo(12); }
    @Test
    void singleNodeIsOneBit(){ assertThat(Solution.sumRootToLeaf(new Solution.TreeNode(1))).isEqualTo(1); }
}


