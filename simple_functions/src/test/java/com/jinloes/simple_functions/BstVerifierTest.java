package com.jinloes.simple_functions;

import com.jinloes.simple_functions.tree.BstVerifier;
import com.jinloes.simple_functions.tree.Node;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class BstVerifierTest {

    @Test
    public void testVerify() {
        Node<Integer> threeLeft = new Node<>(4, null, null);
        Node<Integer> oneLeft = new Node<>(2, null, null);
        Node<Integer> oneRight = new Node<>(3, threeLeft, null);
        Node<Integer> root = new Node<>(1, oneLeft, oneRight);

        assertThat(BstVerifier.verify(root)).isTrue();
    }

    @Test
    public void testVerifyFalse() {
        Node<Integer> threeLeft = new Node<>(4, null, null);
        Node<Integer> oneLeft = new Node<>(2, null, null);
        Node<Integer> oneRight = new Node<>(6, threeLeft, null);
        Node<Integer> root = new Node<>(1, oneLeft, oneRight);

        assertThat(BstVerifier.verify(root)).isFalse();
    }
}
