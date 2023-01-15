package com.jinloes.data_structures.graph;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.junit.jupiter.api.BeforeAll;

public class BaseGraphTest {
    protected Graph<String, DefaultEdge> stringGraph;

    @BeforeAll
    public void setUp() {
        stringGraph = new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        stringGraph.addVertex(v1);
        stringGraph.addVertex(v2);
        stringGraph.addVertex(v3);
        stringGraph.addVertex(v4);

        // add edges to create a circuit
        stringGraph.addEdge(v1, v2);
        stringGraph.addEdge(v2, v3);
        stringGraph.addEdge(v3, v4);
        stringGraph.addEdge(v4, v1);

        System.out.println(stringGraph);
    }
}
