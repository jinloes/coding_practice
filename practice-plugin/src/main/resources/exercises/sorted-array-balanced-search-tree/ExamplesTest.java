package com.jinloes.practice;
import org.junit.jupiter.api.Test; import java.util.*; import static org.assertj.core.api.Assertions.assertThat;
class ExamplesTest {
    @Test
    void buildsThreeValueTree(){ assertTree(new int[]{1,2,3}); }
    @Test
    void emptyArrayReturnsNull(){ assertThat(Solution.buildBalanced(new int[0])).isNull(); }
    @Test
    void buildsOddLengthTree(){ assertTree(new int[]{-2,0,5,9,12}); } static void assertTree(int[] values){ Solution.TreeNode root=Solution.buildBalanced(values); assertThat(inorder(root)).containsExactlyElementsOf(java.util.Arrays.stream(values).boxed().toList()); assertThat(height(root)).isGreaterThanOrEqualTo(0); } static List<Integer> inorder(Solution.TreeNode n){ List<Integer> out=new ArrayList<>(); walk(n,out); return out; } static void walk(Solution.TreeNode n,List<Integer> out){ if(n!=null){walk(n.left,out);out.add(n.value);walk(n.right,out);} } static int height(Solution.TreeNode n){ if(n==null)return 0; int l=height(n.left), r=height(n.right); if(l<0||r<0||Math.abs(l-r)>1)return -1; return Math.max(l,r)+1; } }
