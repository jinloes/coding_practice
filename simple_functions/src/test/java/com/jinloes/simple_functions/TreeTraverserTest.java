package com.jinloes.simple_functions;

import com.jinloes.simple_functions.tree.TreeNode;
import com.jinloes.simple_functions.tree.TreeTraverser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TreeTraverserTest {

    private TreeNode<String> root;

    @BeforeEach
    void setUp() {
        // Level 0:      A
        // Level 1:    B   C
        // Level 2:   D E
        root = new TreeNode<>("A",
            new TreeNode<>("B", new TreeNode<>("D"), new TreeNode<>("E")),
            new TreeNode<>("C"));
    }

    @Test
    void nullRootReturnsEmpty() {
        assertThat(TreeTraverser.preOrder(null)).isEmpty();
        assertThat(TreeTraverser.inOrder(null)).isEmpty();
        assertThat(TreeTraverser.postOrder(null)).isEmpty();
    }

    @Test
    void preOrder() {
        assertThat(TreeTraverser.preOrder(root)).isEqualTo(List.of("A", "B", "D", "E", "C"));
    }

    @Test
    void singleNode() {
        TreeNode<String> single = new TreeNode<>("A");
        assertThat(TreeTraverser.preOrder(single)).isEqualTo(List.of("A"));
        assertThat(TreeTraverser.inOrder(single)).isEqualTo(List.of("A"));
        assertThat(TreeTraverser.postOrder(single)).isEqualTo(List.of("A"));
    }

    @Test
    void rightSkewedTree() {
        // Level 0:  A
        // Level 1:    B
        // Level 2:      C
        TreeNode<String> skewed = new TreeNode<>("A", null, new TreeNode<>("B", null, new TreeNode<>("C")));
        assertThat(TreeTraverser.preOrder(skewed)).isEqualTo(List.of("A", "B", "C"));
        assertThat(TreeTraverser.inOrder(skewed)).isEqualTo(List.of("A", "B", "C"));
        assertThat(TreeTraverser.postOrder(skewed)).isEqualTo(List.of("C", "B", "A"));
    }

    @Test
    void inOrder() {
        assertThat(TreeTraverser.inOrder(root)).isEqualTo(List.of("D", "B", "E", "A", "C"));
    }

    @Test
    void postOrder() {
        assertThat(TreeTraverser.postOrder(root)).isEqualTo(List.of("D", "E", "B", "C", "A"));
    }
}