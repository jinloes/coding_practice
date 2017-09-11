package com.jinloes.simple_functions.graph;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private int size;
    private List<List<Integer>> adjanceyLists;

    public Graph(int size) {
        this.size = size;
        this.adjanceyLists = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            adjanceyLists.add(new ArrayList<>());
        }
    }

    public void addEdge(int source, int target) {
        adjanceyLists.get(source).add(target);
    }

    public List<Integer> getAdjacencyList(int vertex) {
        return adjanceyLists.get(vertex);
    }
}
