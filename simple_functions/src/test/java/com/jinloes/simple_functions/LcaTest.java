package com.jinloes.simple_functions;

import com.jinloes.simple_functions.tree.Lca;
import com.jinloes.simple_functions.tree.Node;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link Lca}.
 */
public class LcaTest {

    @Test
    public void testLCA() {
        Node<Integer> threeLeft = new Node<>(4, null, null);
        Node<Integer> oneLeft = new Node<>(2, null, null);
        Node<Integer> oneRight = new Node<>(3, threeLeft, null);
        Node<Integer> root = new Node<>(1, oneLeft, oneRight);

        assertThat(Lca.find(root, oneLeft, oneRight)).contains(root);
        assertThat(Lca.find(root, oneLeft, new Node<>(100, null, null))).isEmpty();
        assertThat(Lca.find(root, oneLeft, threeLeft)).contains(root);
    }

    @Test
    public void testLcaUsingTraversal() {
        Node<Integer> threeLeft = new Node<>(4, null, null);
        Node<Integer> oneLeft = new Node<>(2, null, null);
        Node<Integer> oneRight = new Node<>(3, threeLeft, null);
        Node<Integer> root = new Node<>(1, oneLeft, oneRight);

        assertThat(Lca.findUsingTraversal(root, oneLeft, oneRight)).contains(root);
        assertThat(Lca.findUsingTraversal(root, oneLeft, threeLeft)).contains(root);
    }
}
