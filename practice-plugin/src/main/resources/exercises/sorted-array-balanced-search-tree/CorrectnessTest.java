package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.*; import static org.assertj.core.api.Assertions.assertThat;
class CorrectnessTest {
    @Test
    void singleValueBecomesRoot(){ Solution.TreeNode root=Solution.buildBalanced(new int[]{42}); assertThat(root.value).isEqualTo(42); assertThat(root.left).isNull(); assertThat(root.right).isNull(); }
    @Test
    void handlesEvenLengthArray(){ assertTree(new int[]{1,2,3,4,5,6}); }
    @Test
    void handlesNegativeValues(){ assertTree(new int[]{-9,-4,-1,0,8}); }
    @Test
    void handlesLargeArray(){ int[] v=new int[511]; for(int i=0;i<v.length;i++)v[i]=i*2; assertTree(v); }
    @Test
    void doesNotModifyInput(){ int[] v={1,2,3,4}; int[] b=v.clone(); Solution.buildBalanced(v); assertThat(v).containsExactly(b); } static void assertTree(int[] values){ Solution.TreeNode root=Solution.buildBalanced(values); assertThat(inorder(root)).containsExactlyElementsOf(java.util.Arrays.stream(values).boxed().toList()); assertThat(height(root)).isGreaterThanOrEqualTo(0); } static List<Integer> inorder(Solution.TreeNode n){ List<Integer> out=new ArrayList<>(); walk(n,out); return out; } static void walk(Solution.TreeNode n,List<Integer> out){ if(n!=null){walk(n.left,out);out.add(n.value);walk(n.right,out);} } static int height(Solution.TreeNode n){ if(n==null)return 0; int l=height(n.left), r=height(n.right); if(l<0||r<0||Math.abs(l-r)>1)return -1; return Math.max(l,r)+1; } }
