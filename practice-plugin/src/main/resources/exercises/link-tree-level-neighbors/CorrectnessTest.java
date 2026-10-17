package com.jinloes.practice;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CorrectnessTest {
    @Test
    void nullRootIsAccepted() { assertThat(Solution.connect(null)).isNull(); }

    @Test
    void singleNodeHasNoNeighbor() {
        Solution.Node root = new Solution.Node(10);
        root.next = new Solution.Node(99);
        Solution.connect(root);
        assertThat(root.next).isNull();
    }

    @Test
    void overwritesOldLinksAcrossSeveralLevels() {
        Solution.Node n4 = new Solution.Node(4);
        Solution.Node n5 = new Solution.Node(5);
        Solution.Node n6 = new Solution.Node(6);
        Solution.Node n7 = new Solution.Node(7);
        Solution.Node root = new Solution.Node(1, new Solution.Node(2, n4, n5), new Solution.Node(3, n6, n7));
        n4.next = root;
        n7.next = root;
        Solution.connect(root);
        assertThat(root.next).isNull();
        assertThat(root.left.next).isSameAs(root.right);
        assertThat(n4.next).isSameAs(n5);
        assertThat(n5.next).isSameAs(n6);
        assertThat(n6.next).isSameAs(n7);
        assertThat(n7.next).isNull();
    }

    @Test
    void sparseDeepTreeLinksOnlyExistingNodes() {
        Solution.Node a = new Solution.Node(8);
        Solution.Node b = new Solution.Node(9);
        Solution.Node c = new Solution.Node(10);
        Solution.Node root = new Solution.Node(1, new Solution.Node(2, null, a), new Solution.Node(3, b, null));
        a.left = c;
        Solution.connect(root);
        assertThat(a.next).isSameAs(b);
        assertThat(b.next).isNull();
        assertThat(c.next).isNull();
    }
}
