package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void returnsAllOrders(){ Solution.TreeNode root=new Solution.TreeNode(1,new Solution.TreeNode(2,null,new Solution.TreeNode(4)),new Solution.TreeNode(3)); Solution.TraversalOrders o=Solution.traversalOrders(root); assertThat(o.preorder()).containsExactly(1,2,4,3); assertThat(o.inorder()).containsExactly(2,4,1,3); assertThat(o.postorder()).containsExactly(4,2,3,1); }
    @Test
    void singleNodeAppearsEverywhere(){ assertThat(Solution.traversalOrders(new Solution.TreeNode(7))).isEqualTo(new Solution.TraversalOrders(List.of(7),List.of(7),List.of(7))); }
}


