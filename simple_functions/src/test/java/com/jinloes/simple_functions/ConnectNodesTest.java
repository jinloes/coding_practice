package com.jinloes.simple_functions;

import com.jinloes.simple_functions.graph.ConnectNodes;
import com.jinloes.simple_functions.graph.Node;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConnectNodesTest {

    @Test
    void singleNodeHasNoConnections() {
        Node root = new Node(null, null, "A");
        ConnectNodes.connectNodes(root);
        assertThat(root.getNext()).isNull();
        assertThat(root.getPrevious()).isNull();
    }

    @Test
    void twoChildrenAreConnected() {
        Node child2 = new Node(null, null, "B");
        Node child3 = new Node(null, null, "C");
        Node root = new Node(child2, child3, "A");
        ConnectNodes.connectNodes(root);
        assertThat(child2.getPrevious()).isNull();
        assertThat(child2.getNext()).isEqualTo(child3);
        assertThat(child3.getPrevious()).isEqualTo(child2);
    }

    @Test
    void grandchildrenAreConnectedAcrossSubtrees() {
        Node child3Child = new Node(null, null, "F");
        Node child2Child = new Node(null, null, "D");
        Node child2Child2 = new Node(null, null, "E");
        Node child2 = new Node(child2Child, child2Child2, "B");
        Node child3 = new Node(child3Child, null, "C");
        Node root = new Node(child2, child3, "A");
        ConnectNodes.connectNodes(root);
        assertThat(child2Child.getPrevious()).isNull();
        assertThat(child2Child.getNext()).isEqualTo(child2Child2);
        assertThat(child2Child2.getPrevious()).isEqualTo(child2Child);
        assertThat(child2Child2.getNext()).isEqualTo(child3Child);
        assertThat(child3Child.getPrevious()).isEqualTo(child2Child2);
        assertThat(child3Child.getNext()).isNull();
    }
}