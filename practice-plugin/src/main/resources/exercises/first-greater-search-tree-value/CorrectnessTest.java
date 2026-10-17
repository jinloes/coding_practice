package com.jinloes.practice;
import org.junit.jupiter.api.Test; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void handlesNullRoot(){ assertThat(Solution.firstGreater(null,5)).isNull(); }
    @Test
    void handlesKeyBelowAllValues(){ assertThat(Solution.firstGreater(tree(10,5,15),0)).isEqualTo(5); }
    @Test
    void handlesKeyBetweenRootAndLeftSubtree(){ assertThat(Solution.firstGreater(tree(20,10,30,25,40),22)).isEqualTo(25); }
    @Test
    void handlesSingleNodeTree(){ assertThat(Solution.firstGreater(new Solution.TreeNode(3),2)).isEqualTo(3); assertThat(Solution.firstGreater(new Solution.TreeNode(3),3)).isNull(); }
    @Test
    void handlesLargeBalancedTree(){ Solution.TreeNode root=balanced(0,1024); assertThat(Solution.firstGreater(root,777)).isEqualTo(778); } static Solution.TreeNode balanced(int lo,int hi){ if(lo>=hi)return null; int mid=(lo+hi)/2; return new Solution.TreeNode(mid, balanced(lo,mid), balanced(mid+1,hi)); } static Solution.TreeNode tree(int... values){ Solution.TreeNode root=null; for(int v:values) root=insert(root,v); return root; } static Solution.TreeNode insert(Solution.TreeNode n,int v){ if(n==null)return new Solution.TreeNode(v); if(v<n.value)n.left=insert(n.left,v); else n.right=insert(n.right,v); return n; } }
