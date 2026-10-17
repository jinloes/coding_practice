package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void rightSubtreeUsesLeftmostNode(){ Solution.TreeNode n8=new Solution.TreeNode(8); Solution.TreeNode root=new Solution.TreeNode(7,new Solution.TreeNode(3),new Solution.TreeNode(10,n8,new Solution.TreeNode(12))); assertThat(Solution.inorderSuccessor(root,root)).isSameAs(n8); }
    @Test
    void duplicateValuesUseIdentity(){ Solution.TreeNode first=new Solution.TreeNode(2); Solution.TreeNode second=new Solution.TreeNode(2); Solution.TreeNode root=new Solution.TreeNode(1,first,second); assertThat(Solution.inorderSuccessor(root,first)).isSameAs(root); }
    @Test
    void missingTargetReturnsNull(){ assertThat(Solution.inorderSuccessor(new Solution.TreeNode(1),new Solution.TreeNode(1))).isNull(); }
}


