package com.jinloes.simple_functions.tree;

import com.jinloes.simple_functions.TreeNode;
import org.assertj.core.util.Lists;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TreeTraverserTest {

  @Test public void testLevelOrderTraversal() {
    TreeNode<Integer> level2_5 = new TreeNode<>(5);
    TreeNode<Integer> level2_4 = new TreeNode<>(4);
    TreeNode<Integer> level1_3 = new TreeNode<>(3);
    TreeNode<Integer> level1_2 = new TreeNode<>(2, Lists.newArrayList(level2_4, level2_5));
    TreeNode<Integer> root = new TreeNode<>(1, Lists.newArrayList(level1_2, level1_3));

    assertThat(TreeTraverser.breadthTraversal(null)).isEmpty();
    assertThat(TreeTraverser.breadthTraversal(root)).usingElementComparatorIgnoringFields("children")
        .containsExactly(new TreeNode<>(1), new TreeNode<>(2), new TreeNode<>(3), new TreeNode<>(4), new TreeNode<>(5));
  }

  @Test public void testPreorderIterative() {
    /*
          1
         2 3
        4 5
     */
    TreeNode<Integer> level2_5 = new TreeNode<>(5);
    TreeNode<Integer> level2_4 = new TreeNode<>(4);
    TreeNode<Integer> level1_3 = new TreeNode<>(3);
    TreeNode<Integer> level1_2 = new TreeNode<>(2, Lists.newArrayList(level2_4, level2_5));
    TreeNode<Integer> root = new TreeNode<>(1, Lists.newArrayList(level1_2, level1_3));

    assertThat(TreeTraverser.preorderIterative(null)).isEmpty();
    assertThat(TreeTraverser.preorderIterative(root)).usingElementComparatorIgnoringFields("children")
        .containsExactly(new TreeNode<>(1), new TreeNode<>(2), new TreeNode<>(4), new TreeNode<>(5), new TreeNode<>(3));
  }
}