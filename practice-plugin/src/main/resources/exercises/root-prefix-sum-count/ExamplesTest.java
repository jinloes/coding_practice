package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void countsTwoPrefixes(){ Solution.TreeNode root=new Solution.TreeNode(5,new Solution.TreeNode(3,new Solution.TreeNode(2),new Solution.TreeNode(1)),new Solution.TreeNode(-1,null,new Solution.TreeNode(4))); assertThat(Solution.countRootPrefixes(root,8)).isEqualTo(2); }
    @Test
    void rootCanCount(){ assertThat(Solution.countRootPrefixes(new Solution.TreeNode(1,new Solution.TreeNode(2),new Solution.TreeNode(3)),1)).isEqualTo(1); }
}



