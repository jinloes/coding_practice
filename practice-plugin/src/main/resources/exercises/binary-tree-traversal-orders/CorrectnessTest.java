package com.jinloes.practice;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void emptyTreeHasEmptyLists(){ assertThat(Solution.traversalOrders(null)).isEqualTo(new Solution.TraversalOrders(List.of(),List.of(),List.of())); }
    @Test
    void leftChainOrdersDiffer(){ Solution.TreeNode root=new Solution.TreeNode(3,new Solution.TreeNode(2,new Solution.TreeNode(1),null),null); Solution.TraversalOrders o=Solution.traversalOrders(root); assertThat(o.preorder()).containsExactly(3,2,1); assertThat(o.inorder()).containsExactly(1,2,3); assertThat(o.postorder()).containsExactly(1,2,3); }
    @Test
    void rightChainPostorderReverses(){ Solution.TreeNode root=new Solution.TreeNode(1,null,new Solution.TreeNode(2,null,new Solution.TreeNode(3))); assertThat(Solution.traversalOrders(root).postorder()).containsExactly(3,2,1); }
}


