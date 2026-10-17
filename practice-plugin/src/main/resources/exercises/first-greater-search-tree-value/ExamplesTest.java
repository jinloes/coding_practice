package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void findsNextValueAboveMissingKey(){ assertThat(Solution.firstGreater(tree(5,3,8,2,4,7,9),6)).isEqualTo(7); }
    @Test
    void returnsNullWhenNoGreaterValueExists(){ assertThat(Solution.firstGreater(tree(5,3,8,2,4,7,9),9)).isNull(); }
    @Test
    void requiresStrictlyGreaterValue(){ assertThat(Solution.firstGreater(tree(5,3,8,2,4,7,9),4)).isEqualTo(5); } static Solution.TreeNode tree(int... values){ Solution.TreeNode root=null; for(int v:values) root=insert(root,v); return root; } static Solution.TreeNode insert(Solution.TreeNode n,int v){ if(n==null)return new Solution.TreeNode(v); if(v<n.value)n.left=insert(n.left,v); else n.right=insert(n.right,v); return n; } }
