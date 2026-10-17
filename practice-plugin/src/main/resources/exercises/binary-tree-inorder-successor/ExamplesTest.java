package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsRootAfterLeftSubtree(){ Solution.TreeNode n3=new Solution.TreeNode(3); Solution.TreeNode root=new Solution.TreeNode(4,new Solution.TreeNode(2,new Solution.TreeNode(1),n3),new Solution.TreeNode(6,new Solution.TreeNode(5),new Solution.TreeNode(7))); assertThat(Solution.inorderSuccessor(root,n3)).isSameAs(root); }
    @Test
    void lastNodeHasNoSuccessor(){ Solution.TreeNode n7=new Solution.TreeNode(7); Solution.TreeNode root=new Solution.TreeNode(4,new Solution.TreeNode(2),new Solution.TreeNode(6,null,n7)); assertThat(Solution.inorderSuccessor(root,n7)).isNull(); }
}


